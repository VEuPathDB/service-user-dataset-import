package org.veupathdb.service.userds.controller;

import java.io.InputStream;
import java.sql.Date;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;

import com.devskiller.friendly_id.FriendlyId;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.veupathdb.lib.container.jaxrs.middleware.AuthFilter;
import org.veupathdb.lib.container.jaxrs.providers.UserProvider;
import org.veupathdb.service.userds.Main;
import org.veupathdb.service.userds.generated.model.*;
import org.veupathdb.service.userds.generated.resources.UserDatasets;
import org.veupathdb.service.userds.model.JobRow;
import org.veupathdb.service.userds.model.JobStatus;
import org.veupathdb.service.userds.model.Service;
import org.veupathdb.service.userds.repo.InsertJobQuery;
import org.veupathdb.service.userds.repo.SelectJobQuery;
import org.veupathdb.service.userds.repo.SelectJobsQuery;
import org.veupathdb.service.userds.service.Importer;
import org.veupathdb.service.userds.util.ErrFac;
import org.veupathdb.service.userds.util.Errors;
import org.veupathdb.service.userds.util.InputStreamNotifier;

@AuthFilter.Authenticated
public class UserDatasetSvc implements UserDatasets
{
  static final String
    errRowFetch = "failed to fetch user's job rows";


  private final Logger  log;
  private final Request req;

  public UserDatasetSvc(@Context Request req) {
    this.req = req;
    this.log = LogManager.getLogger(getClass());
  }

  @Override
  public GetResponse getUserDatasets() {
    final List < JobRow > rows;

    // orElseThrow as this should not be possible
    var user = UserProvider.lookupUser(req).orElseThrow();

    try {
      rows = SelectJobsQuery.run(user.getUserId());
    } catch (SQLException e) {
      log.error(errRowFetch, e);
      return GetResponse.respond500(ErrFac.new500(req, e));
    }

    return GetResponse.respond200(rows.stream()
      .map(UDSvcUtil::rowToStatus)
      .collect(Collectors.toList()));
  }

  @Override
  public PostResponse postUserDatasets(PrepRequest entity) {
    var val = UDSvcUtil.validate(entity);

    if (val.isPresent()) {
      var out = new InvalidInputErrorImpl();
      out.setErrors(val.get());
      return PostResponse.respond422(out);
    }

    try {
      var user  = UserProvider.lookupUser(req).orElseThrow();
      var jobId = FriendlyId.createFriendlyId();
      var job   = UDSvcUtil.prepToJob(entity, jobId, user.getUserId());

      InsertJobQuery.run(job);

      return PostResponse.respond200(new PrepResponseImpl().setJobId(jobId));
    } catch (Throwable e) {
      log.error("Failed to create new job entry", e);
      return PostResponse.respond500(ErrFac.new500(req, e));
    }
  }

  @Override
  public GetByJobIdResponse getUserDatasetsByJobId(String jobId) {
    try {
      var optJob = SelectJobQuery.run(jobId);

      if (optJob.isEmpty())
        return GetByJobIdResponse.respond404(ErrFac.new404());

      return GetByJobIdResponse.respond200(UDSvcUtil.rowToStatus(optJob.get()));
    } catch (SQLException e) {
      log.error(errRowFetch, e);
      return GetByJobIdResponse.respond500(ErrFac.new500(req, e));
    }
  }

  @Override
  public PostByJobIdResponse postUserDatasetsByJobId(
    final String jobId,
    final InputStream body
  ) {
    try {
      var optJob = SelectJobQuery.run(jobId);

      if (optJob.isEmpty())
        return PostByJobIdResponse.respond404(ErrFac.new404());

      var pipeWrap = new InputStreamNotifier(body, this);

      new Thread(new Importer(optJob.get(), pipeWrap)).start();

      wait();

    } catch (Throwable e) {
      log.error(e);
      return PostByJobIdResponse.respond500(ErrFac.new500(req, e));
    } finally {
      Errors.swallow(body::close);
    }

    return PostByJobIdResponse.respond200(new ProcessResponseImpl());
  }
}

class UDSvcUtil
{
  static StatusResponse rowToStatus(JobRow row) {
    return new StatusResponseImpl()
      .setId(row.getJobId())
      .setDatasetName(row.getName())
      .setDescription(row.getDescription().orElse(null))
      .setSummary(row.getSummary().orElse(null))
      .setStatus(row.getStatus().getName())
      .setProjects(row.getProjects())
      .setStarted(Date.from(
        row.getStarted()
          .atZone(ZoneId.systemDefault())
          .toInstant()));
  }

  static JobRow prepToJob(PrepRequest body, String jobId, long userId) {
    return new JobRow(0, jobId, userId, JobStatus.AWAITING_UPLOAD,
      body.getDatasetName(), body.getDescription(), body.getSummary(), null,
      null, null, body.getProjects());
  }

  static Optional < InvalidInputError.ErrorsType > validate(PrepRequest body) {
    var respond = false;
    var out     = new InvalidInputErrorImpl.ErrorsTypeImpl();
    out.setByKey(new InvalidInputErrorImpl.ErrorsTypeImpl.ByKeyTypeImpl());

    if (body.getDatasetName() == null || body.getDatasetName().isBlank()) {
      respond = true;
      out.getByKey().setAdditionalProperties(
        "datasetName",
        "Dataset name cannot be blank."
      );
    }

    if (body.getProjects() == null || body.getProjects().isEmpty()) {
      respond = true;
      out.getByKey().setAdditionalProperties(
        "projects",
        "At least one target project must be provided."
      );
    }

    if (body.getDatasetType() == null || body.getDatasetType().isBlank()) {
      respond = true;
      out.getByKey().setAdditionalProperties(
        "datasetType",
        "Dataset type cannot be blank."
      );
    } else if (
      Arrays.stream(Main.jsonConfig.getServices())
        .map(Service::getDsType)
        .anyMatch(body.getDatasetType()::equals)
    ) {
      respond = true;
      out.getByKey().setAdditionalProperties(
        "datasetType",
        "Unsupported dataset type."
      );
    }

    return respond ? Optional.of(out) : Optional.empty();
  }
}
