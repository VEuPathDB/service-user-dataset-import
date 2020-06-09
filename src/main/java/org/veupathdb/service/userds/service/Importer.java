package org.veupathdb.service.userds.service;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.logging.log4j.Logger;
import org.veupathdb.lib.container.jaxrs.providers.LogProvider;
import org.veupathdb.service.userds.model.IrodsStatus;
import org.veupathdb.service.userds.model.JobRow;
import org.veupathdb.service.userds.model.JobStatus;
import org.veupathdb.service.userds.model.handler.HandlerGeneralError;
import org.veupathdb.service.userds.model.handler.HandlerJobResult;
import org.veupathdb.service.userds.model.handler.HandlerValidationError;
import org.veupathdb.service.userds.repo.InsertMessageQuery;
import org.veupathdb.service.userds.repo.UpdateJobCompletedQuery;
import org.veupathdb.service.userds.repo.UpdateJobStatusQuery;
import org.veupathdb.service.userds.util.Errors;

import static org.veupathdb.service.userds.util.Format.Json;

public class Importer implements Runnable
{
  private static final Logger log = LogProvider.logger(Importer.class);

  private final JobRow      job;
  private final String      boundary;
  private final InputStream reader;

  public Importer(
    JobRow job,
    String boundary,
    InputStream reader
  ) {
    this.job = job;
    this.boundary = boundary;
    this.reader = reader;
  }

  public void run() {
    log.trace("Importer#run");
    try {
      final var hand = Handler.getHandler("biom").orElseThrow();

      if (!doPrep(hand))
        return;

      UpdateJobStatusQuery.run(job.getDbId(), JobStatus.SENDING_TO_HANDLER);

      var result = doSubmit(hand);

      if (result.isEmpty())
        return;

      try {
        doStore(result.get());
      } finally {
        Errors.swallow(() -> result.get().getContent().close());
      }

      UpdateJobStatusQuery.run(job.getDbId(), JobStatus.DATASTORE_UNPACKING);

      var flag = Optional.< IrodsStatus >empty();
      var file = result.get().getFileName().substring(0, result.get().getFileName().indexOf('.'));

      while (flag.isEmpty()) {
        // iRODS is already struggling just to do iRODS stuff, no need to
        // harass it.
        Thread.sleep(500);

        flag = Irods.getFlag(file);
      }

      if (flag.get() == IrodsStatus.SUCCESS) {
        UpdateJobStatusQuery.run(job.getDbId(), JobStatus.SUCCESS);
      } else {
        InsertMessageQuery.run(job.getDbId(), "Datastore failed to unpack dataset");
        UpdateJobStatusQuery.run(job.getDbId(), JobStatus.ERRORED);
      }

      UpdateJobCompletedQuery.run(job.getDbId(), LocalDateTime.now());
    } catch (Throwable e) {
      LogProvider.logger(getClass())
        .error("Failed to submit job to handler", e);
      Errors.swallow(() ->
        UpdateJobStatusQuery.run(job.getDbId(), JobStatus.ERRORED));
      Errors.swallow(() ->
        InsertMessageQuery.run(job.getDbId(), e.getMessage()));
    } finally {
      Errors.swallow(reader::close);
    }
  }

  private boolean doPrep(Handler hand) throws Exception {
    var raw = hand.prepareJob(job);

    if (raw.isEmpty())
      return true;

    var res = raw.get();

    if (res.isLeft()) {
      var val = res.leftOrThrow();
      switch (val.getCode()) {
        case 400, 401 -> do400(val);
        default -> do500(val);
      }
      return false;
    }

    do422(res.rightOrThrow());
    return false;
  }

  private Optional< HandlerJobResult > doSubmit(Handler hand) throws Exception {
    var raw = hand.submitJob(job, boundary, reader);

    if (raw.isLeft())
      return Optional.of(raw.leftOrThrow());

    var errs = raw.rightOrThrow();

    if (errs.isLeft()) {
      var val = errs.leftOrThrow();
      switch (val.getCode()) {
        case 400, 401 -> do400(val);
        default -> do500(val);
      }
    } else {
      do422(errs.rightOrThrow());
    }

    return Optional.empty();
  }

  private void doStore(HandlerJobResult result) throws Exception {
    LogProvider.logger(getClass()).trace("Importer#doStore");
    try {
      Irods.writeDataset(result.getFileName(), result.getContent());
    } catch (Throwable t) {
      log.debug("failed to write dataset", t);
      throw t;
    } finally {
      result.getContent().close();
    }
  }

  private void do400(HandlerGeneralError err) throws Exception {
    LogProvider.logger(getClass()).trace("Importer#do400");
    UpdateJobStatusQuery.run(job.getDbId(), JobStatus.REJECTED);
    UpdateJobCompletedQuery.run(job.getDbId(), LocalDateTime.now());
    InsertMessageQuery.run(job.getDbId(), err.getMessage());
  }

  private void do422(HandlerValidationError err) throws Exception {
    LogProvider.logger(getClass()).trace("Importer#do422");
    var js = Json.convertValue(err.getErrors(), JsonNode.class);
    UpdateJobStatusQuery.run(job.getDbId(), JobStatus.REJECTED);
    UpdateJobCompletedQuery.run(job.getDbId(), LocalDateTime.now());
    InsertMessageQuery.run(job.getDbId(), js);
  }

  private void do500(HandlerGeneralError err) throws Exception {
    do500(err.getMessage());
  }

  private void do500(String err) throws Exception {
    LogProvider.logger(getClass()).trace("Importer#do500");
    UpdateJobStatusQuery.run(job.getDbId(), JobStatus.ERRORED);
    UpdateJobCompletedQuery.run(job.getDbId(), LocalDateTime.now());
    InsertMessageQuery.run(job.getDbId(), err);
  }
}
