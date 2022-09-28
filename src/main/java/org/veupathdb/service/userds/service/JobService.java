package org.veupathdb.service.userds.service;

import com.devskiller.friendly_id.FriendlyId;
import com.fasterxml.jackson.databind.JsonNode;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.veupathdb.service.userds.Main;
import org.veupathdb.service.userds.generated.model.*;
import org.veupathdb.service.userds.model.JobRow;
import org.veupathdb.service.userds.model.JobStatus;
import org.veupathdb.service.userds.model.MetaValidationResult;
import org.veupathdb.service.userds.model.ProjectCache;
import org.veupathdb.service.userds.model.handler.DatasetOrigin;
import org.veupathdb.service.userds.repo.DeleteJobQuery;
import org.veupathdb.service.userds.repo.InsertJobQuery;
import org.veupathdb.service.userds.repo.SelectJobQuery;
import org.veupathdb.service.userds.repo.SelectJobsQuery;

import static java.util.Collections.singletonList;

public class JobService
{
  /**
   * Look up existing jobs owned by the given user id.
   *
   * @param userId User id to lookup.
   *
   * @return A list of jobs owned by the given user id.  If no jobs were found,
   * the returned list will be empty.
   */
  public static List < JobRow > getJobsByUser(long userId, int limit, int page)
  throws Exception {
    return SelectJobsQuery.run(userId, limit, page);
  }

  public static void deleteJobById(int dbId) throws Exception {
    DeleteJobQuery.run(dbId);
  }

  /**
   * Look up an existing job by the given job token/id string.
   *
   * @param jobId Job id to lookup.
   *
   * @return An option which will contain a job instance if a matching job was
   * found.
   */
  public static Optional < JobRow > getJobByToken(String jobId)
  throws Exception {
    return SelectJobQuery.run(jobId);
  }

  public static String insertJob(PrepRequest req, long userId)
  throws Exception {
    final var jobId = FriendlyId.createFriendlyId();
    InsertJobQuery.run(prepToJob(req, jobId, userId));
    return jobId;
  }

  private static final String
    valErrBlankName = "Dataset name cannot be blank.";

  /**
   * Perform input data validation for a job creation request body.
   *
   * @param req Job creation request body
   *
   * @return An option which will contain a validation error set if validation
   * fails.
   */
  public static Optional < MetaValidationResult > validateJobMeta(
    final PrepRequest req
  ) {
    var out = new MetaValidationResult();

    // Verify the request has a non-empty name
    if (req.getDatasetName() == null || req.getDatasetName().isBlank()) {
      out.getByKey().put("datasetName", singletonList(valErrBlankName));
    }

    // Verify that there are handlers configured for the selected jobs
    var projects = validateProjectsKey(req.getProjects());
    if (!projects.isEmpty()) {
      out.getByKey().put("projects", projects);
    }

    var type = validateTypeKey(req.getDatasetType(), req);
    if (!type.isEmpty()) {
      out.getByKey().put("datasetType", type);
    }

    var formatParams = validateFormatParams(req.getFormatParams());
    if (!formatParams.isEmpty()) {
      out.getByKey().put("formatParams", formatParams);
    }

    return out.containsErrors() ? Optional.of(out) : Optional.empty();
  }

  /**
   * Convert a JobRow instance to an output StatusResponse instance.
   *
   * @param row Row to convert.
   *
   * @return StatusResponse object.
   */
  public static StatusResponse rowToStatus(JobRow row) {
    var out = new StatusResponseImpl();
    out.setId(row.getJobId());
    out.setDatasetName(row.getName());
    out.setDescription(row.getDescription().orElse(null));
    out.setSummary(row.getSummary().orElse(null));
    out.setStatus(row.getStatus().toApiStatus());
    out.setProjects(row.getProjects());
    out.setStarted(Date.from(
        row.getStarted()
          .atZone(ZoneId.systemDefault())
          .toInstant()));
    out.setFinished(row.getFinished()
        .map(d -> d.atZone(ZoneId.systemDefault()))
        .map(ZonedDateTime::toInstant)
        .map(Date::from)
        .orElse(null));
    out.setDatasetId(row.getIrodsId());
    out.setFormatParams(row.getFormatParams().orElse(null));

    // If the job status was "errored" then we only have an exception message
    // to return.
    if (row.getStatus() == JobStatus.ERRORED) {
      JobError error = new JobErrorImpl();
      error.setMessage(row.getMessage().map(JsonNode::textValue).orElse(null));
      out.setStatusDetails(new StatusResponseImpl.StatusDetailsTypeImpl(error));

    // If the job status was "rejected" then we have validation errors to
    // return.
    } else if (row.getStatus() == JobStatus.REJECTED && row.getMessage().isPresent()) {
      var dets = new ValidationErrorsImpl();
      var raw  = row.getMessage().get();
      dets.setErrors(new ValidationErrorsImpl.ErrorsTypeImpl());

      if (raw.has("general")) {
        dets.getErrors().setGeneral(new ArrayList<>());
        raw.get("general")
          .forEach(j -> dets.getErrors().getGeneral().add(j.textValue()));
      }

      if (raw.has("byKey")) {
        dets.getErrors().setByKey(new ValidationErrorsImpl.ErrorsTypeImpl.ByKeyTypeImpl());
        var obj = raw.get("byKey");
        obj.fieldNames()
          .forEachRemaining(k -> dets.getErrors()
            .getByKey()
            .setAdditionalProperties(k, obj.get(k)));
      }

      out.setStatusDetails(new StatusResponseImpl.StatusDetailsTypeImpl(dets));
    }

    return out;
  }

  /**
   * Convert a job prep request to a dummy job row for inserting into the
   * database.
   */
  public static JobRow prepToJob(PrepRequest body, String jobId, long userId) {
    return new JobRow(jobId, userId, JobStatus.AWAITING_UPLOAD,
      body.getDatasetName(), body.getDescription(), body.getSummary(), body.getFormatParams(),
      body.getProjects(), DatasetOrigin.fromApiOrigin(body.getOrigin()), body.getDatasetType(), LocalDateTime.now());
  }

  /**
   * Error messages for input "projects" key validation.
   */
  private static final String
    valErrBadProject = "Unrecognized project %s",
    valErrNoProjects = "At least one target project must be provided.",
    valErrNoProHands = "No handlers configured for project %s";

  /**
   * Input "projects" key validation
   */
  private static List < String > validateProjectsKey(List < String > projects) {
    final var out = new ArrayList < String >();

    // Verify the request has a non-empty project array
    if (projects == null || projects.isEmpty()) {
      out.add(valErrNoProjects);
    } else {
      projects.stream()
        // Filter out and warn about bad projects
        .filter(p -> {
          if (!ProjectCache.getInstance().containsKey(p)) {
            out.add(String.format(valErrBadProject, p));
            return false;
          }
          return true;
        })

        // Warn about projects with no handlers
        .forEach(p -> {
          if (!Main.jsonConfig.getProjects().contains(p)) {
            out.add(String.format(valErrNoProHands, p));
          }
        });
    }

    return out;
  }

  private static final String
      valErrMissingKey = "Missing format parameter key",
      valErrMissingValue = "Missing format parameter value for param with key %s";

  private static List < String > validateFormatParams(List < FormatParam > formatParams) {
    final var out = new ArrayList < String >();
    if (formatParams == null) {
      // format params are optional, return no errors if they are missing.
      return out;
    }
    formatParams.forEach(param -> {
      if (param.getKey() == null || param.getKey().isEmpty()) {
        out.add(valErrMissingKey);
      }
      if (param.getValue() == null || param.getValue().isEmpty()) {
        out.add(String.format(valErrMissingValue, param.getKey()));
      }
    });
    return out;
  }

  /**
   * Error messages for input "datasetType" key validation.
   */
  private static final String
    valErrBlankType   = "Dataset type cannot be blank.",
    valErrNoHandlers  = "No handlers configured for dataset type %s.",
    valErrUnsupported = "Project %s has no handlers for dataset type %s.";

  /**
   * Input "datasetType" key validation.
   */
  private static List < String > validateTypeKey(String type, PrepRequest req) {
    final var out = new ArrayList < String >();

    // Verify the request has a non-empty dataset type that matches with the
    // configured services.
    if (type == null || type.isBlank()) {
      out.add(valErrBlankType);
    } else if (!Main.jsonConfig.getByType().containsKey(type)) {
      out.add(String.format(valErrNoHandlers, type));
    } else {
      var set = Main.jsonConfig.getByType().get(type);

      for (var p : req.getProjects()) {
        if (!set.contains(p)) {
          out.add(String.format(valErrUnsupported, p, type));
        }
      }
    }

    return out;
  }
}
