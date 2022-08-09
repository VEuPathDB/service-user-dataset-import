package org.veupathdb.service.userds.controller;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.veupathdb.lib.container.jaxrs.errors.UnprocessableEntityException;
import org.veupathdb.lib.container.jaxrs.model.User;
import org.veupathdb.lib.container.jaxrs.providers.UserProvider;
import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated;
import org.veupathdb.service.userds.generated.model.*;
import org.veupathdb.service.userds.generated.resources.UserDatasets;
import org.veupathdb.service.userds.model.JobStatus;
import org.veupathdb.service.userds.model.handler.DatasetOrigin;
import org.veupathdb.service.userds.repo.SelectJobsQuery;
import org.veupathdb.service.userds.service.Importer;
import org.veupathdb.service.userds.service.JobService;
import org.veupathdb.service.userds.service.ThreadProvider;
import org.veupathdb.service.userds.service.metrics.ImportMetrics;
import org.veupathdb.service.userds.util.InputStreamNotifier;

import static org.veupathdb.service.userds.service.JobService.deleteJobById;
import static org.veupathdb.service.userds.service.JobService.getJobByToken;
import static org.veupathdb.service.userds.service.JobService.getJobsByUser;
import static org.veupathdb.service.userds.service.JobService.validateJobMeta;

@Authenticated
public class UserDatasetController implements UserDatasets
{
  static final String
    errRowFetch      = "failed to fetch user's job rows",
    errJobCreate     = "failed to create new job entry",
    errProcessImport = "error when processing import",
    errContentType   = "missing or invalid Content-Type header",
    errDoubleStart   = "cannot resubmit an upload to a started job",
    errDelJobRunning = "cannot delete a job that is in progress";

  private final Logger log;

  private final ContainerRequest req;

  private final HttpHeaders headers;

  public UserDatasetController(
    final @Context ContainerRequest req,
    final @Context HttpHeaders headers
  ) {
    this.req = req;
    this.headers = headers;
    this.log = LogManager.getLogger(getClass());
  }

  @Override
  public GetUserDatasetsResponse getUserDatasets(Integer limit, Integer page) {
    if (limit != null) {
      if (limit < 0) {
        throw new BadRequestException("limit must not be less than 0");
      }
    } else {
      limit = SelectJobsQuery.DEFAULT_LIMIT;
    }

    if (page != null) {
      if (page < 0) {
        throw new BadRequestException("page must not be less than 0");
      }
    } else {
      page = SelectJobsQuery.DEFAULT_OFFSET;
    }

    try {
      return GetUserDatasetsResponse.respond200WithApplicationJson(getJobsByUser(UserProvider.lookupUser(req)
        .map(User::getUserID)
        .orElseThrow(), limit, page)
        .stream()
        .map(JobService::rowToStatus)
        .collect(Collectors.toList()));
    } catch (Exception e) {
      throw toRuntimeException(errRowFetch, e);
    }
  }

  @Override
  public GetUserDatasetsByJobIdResponse getUserDatasetsByJobId(String jobId) {
    try {
      return getJobByToken(jobId)
        .map(JobService::rowToStatus)
        .map(GetUserDatasetsByJobIdResponse::respond200WithApplicationJson)
        .orElseThrow(NotFoundException::new);
    } catch (Exception e) {
      throw toRuntimeException(errRowFetch, e);
    }
  }

  @Override
  public PostUserDatasetsResponse postUserDatasets(PrepRequest entity) {
    validateJobMeta(entity).ifPresent(r -> {
      throw new UnprocessableEntityException(r.getGeneral(), r.getByKey());
    });

    // TODO: This field will become required when Galaxy runs imports through
    //       this service.
    if (entity.getOrigin() == null)
      entity.setOrigin(DatasetOrigin.DIRECT_UPLOAD.toApiOrigin());

    try {
      String jobId = JobService.insertJob(entity, UserProvider.lookupUser(req).map(User::getUserID).orElseThrow());
      PrepResponse response = new PrepResponseImpl();
      response.setJobId(jobId);
      return PostUserDatasetsResponse.respond200WithApplicationJson(response);
    }
    catch (Throwable e) {
      throw toRuntimeException(errJobCreate, e);
    }
  }

  @Override
  public DeleteUserDatasetsByJobIdResponse deleteUserDatasetsByJobId(String jobId) {
    try {
      var job = getJobByToken(jobId).orElseThrow(NotFoundException::new);

      switch (job.getStatus()) {
        case AWAITING_UPLOAD, REJECTED, ERRORED, SUCCESS -> deleteJobById(job.getDbId());
        default -> throw new BadRequestException(errDelJobRunning);
      }
      return DeleteUserDatasetsByJobIdResponse.respond204();
    } catch (WebApplicationException e) {
      // Don't catch Jax-RS exceptions.
      throw e;
    } catch (Exception e) {
      log.error(errRowFetch, e);
      throw new InternalServerErrorException(e);
    }
  }

  @Override
  public PostUserDatasetsByJobIdResponse postUserDatasetsByJobId(
    String jobId,
    String uploadType,
    InputStream file,
    FormDataContentDisposition meta,
    String url
  ) {
    log.debug(String.format("Posting user datasets with jobId %s and uploadType %s", jobId, uploadType));
    try (NamedStream namedStream = switch(uploadType) {
      case "file" -> new NamedStream(meta.getFileName(), file);
      case "url"  -> new NamedStream(getFilenameFromURL(url), new URL(url).openStream());
      default     -> throw new UnprocessableEntityException(Map.of(
        "uploadType",
        List.of("Invalid upload type, must be one of \"file\" or \"url\"")
      ));
    }) {
      var job = getJobByToken(jobId)
        .orElseThrow(NotFoundException::new);

      if (job.getStatus() != JobStatus.AWAITING_UPLOAD)
        throw new BadRequestException(errDoubleStart);

      var lock = new Object();

      //noinspection SynchronizationOnLocalVariableOrMethodParameter
      synchronized (lock) {
        ThreadProvider.newThread(new Importer(
          job,
          namedStream.getName(),
          new InputStreamNotifier(namedStream.getInputStream(), lock)
        )).start();
        lock.wait();
      }

      ImportMetrics.emitSuccessfulUploadByType(1, job.getType());
      return PostUserDatasetsByJobIdResponse.respond200WithApplicationJson(new ProcessResponseImpl());
    } catch (WebApplicationException e) {
      // Don't catch Jax-RS exceptions.
      throw e;
    } catch (Throwable e) {
      throw toRuntimeException(errProcessImport, e);
    }
  }

  private RuntimeException toRuntimeException(String logMessage, Throwable e) {
    log.error(logMessage, e);
    return (e instanceof RuntimeException ? (RuntimeException)e : new RuntimeException(logMessage, e));
  }

  private static String getFilenameFromURL(String url) {
    try {
      var path = new URL(url).getPath();
      return path.substring(path.lastIndexOf('/') + 1);
    } catch (MalformedURLException e) {
      throw new BadRequestException("Invalid datasetUrl.");
    }
  }

  private static class NamedStream implements AutoCloseable {
    private final String name;
    private final InputStream inputStream;

    public NamedStream(String name, InputStream inputStream) {
      this.name = name;
      this.inputStream = inputStream;
    }

    public InputStream getInputStream() {
      return inputStream;
    }

    public String getName() {
      return name;
    }

    @Override
    public void close() throws Exception {
      this.inputStream.close();
    }
  }

}
