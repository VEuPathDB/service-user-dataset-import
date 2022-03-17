package org.veupathdb.service.userds.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.*;
import jakarta.ws.rs.core.MediaType;

import io.vulpine.lib.iffy.Either;
import org.apache.logging.log4j.Logger;
import org.veupathdb.lib.container.jaxrs.providers.LogProvider;
import org.veupathdb.service.userds.Main;
import org.veupathdb.service.userds.model.JobRow;
import org.veupathdb.service.userds.model.Service;
import org.veupathdb.service.userds.model.handler.HandlerGeneralError;
import org.veupathdb.service.userds.model.handler.HandlerJobResult;
import org.veupathdb.service.userds.model.handler.HandlerPayload;
import org.veupathdb.service.userds.model.handler.HandlerValidationError;
import org.veupathdb.service.userds.util.Errors;
import org.veupathdb.service.userds.util.Format;
import org.veupathdb.service.userds.util.InputStreamNotifier;
import org.veupathdb.service.userds.util.http.Header;

import static java.lang.String.format;
import static org.veupathdb.service.userds.util.Format.Json;

public class Handler
{
  private static final String
    jobEndpoint    = "http://%s/job/%s",
    statusEndpoint = jobEndpoint + "/status",
    MULTIPART_HEAD = MediaType.MULTIPART_FORM_DATA + "; boundary=",
    fileName       = "filename=";

  private static final Map < String, Handler > handlers = Collections
    .synchronizedMap(new HashMap <>());

  private static final Logger logger = LogProvider.logger(Handler.class);

  private final Service svc;

  public Handler(Service svc) {
    this.svc = svc;
  }

  // TODO: This should return something useful.
  public Optional < Either < HandlerGeneralError, HandlerValidationError > >
  prepareJob(final JobRow job) throws Exception {
    logger.trace("Handler#prepareJob(JobRow)");

    var res = HttpClient.newHttpClient()
      .send(
        HttpRequest.newBuilder(
          URI.create(
            format(jobEndpoint, svc.getName(), job.getJobId())
          )
        )
          .PUT(
            BodyPublishers.ofString(
              Json.writeValueAsString(
                new HandlerPayload(
                  job.getName(),
                  job.getUserId(),
                  job.getProjects(),
                  job.getOrigin(),
                  job.getDescription().orElse(null),
                  job.getSummary().orElse(null)
                )
              )
            )
          )
          .header(Header.CONTENT_TYPE, MediaType.APPLICATION_JSON)
          .build(),
        BodyHandlers.ofString()
      );

    logger.debug("Handler responded with code {}", res.statusCode());

    return switch (res.statusCode()) {
      case 204 -> Optional.empty();
      case 422 -> Optional.of(Either.ofRight(Json.readValue(
        res.body(),
        HandlerValidationError.class
      )));
      default -> Optional.of(Either.ofLeft(Json.readValue(
        res.body(),
        HandlerGeneralError.class
      )));
    };
  }

  /**
   * Submit handler job.
   *
   * @param job      Job Details
   * @param boundary HTTP multipart/form-data boundary string
   * @param body     Upload file
   *
   * @return Result of either a success response or an error.  The error may be
   * either a general error or a validation error.
   */
  public Either < HandlerJobResult, Either < HandlerGeneralError, HandlerValidationError > >
  submitJob(
    final JobRow job,
    final String boundary,
    final InputStreamNotifier body
  ) throws Exception {
    logger.trace("Handler#submitJob(JobRow, String, InputStream)");
    try {
      final var res = HttpClient.newHttpClient().send(
        HttpRequest.newBuilder(URI.create(
          format(jobEndpoint, svc.getName(), job.getJobId())))
          .header(Header.CONTENT_TYPE, MULTIPART_HEAD + boundary)
          .POST(BodyPublishers.ofInputStream(() -> body))
          .build(),
        BodyHandlers.ofInputStream()
      );
      Errors.swallow(body::close);

      logger.debug("Upload sent to handler.  Total {} bytes.", body.bytesRead());

      // Client connection should be closed with the body.close call.  From here
      // on we need to make sure we log errors because Jersey won't do any
      // reporting for us.

      if (res.statusCode() == 200) {
        logger.debug("job submission successful");
        var optName = res.headers()
          .allValues(Header.CONTENT_DISPOSITION)
          .stream()
          .filter(v -> v.startsWith(Handler.fileName))
          .findAny()
          .map(v -> v.substring(Handler.fileName.length()));

        if (optName.isEmpty()) {
          var msg = "invalid response from handler:  no filename header.";
          logger.error(msg);
          throw new Exception(msg);
        }

        return Either.ofLeft(new HandlerJobResult(optName.get(), res.body()));
      } else {
        logger.warn("job submission unsuccessful");
      }

      return res.statusCode() == 422
        ? Either.ofRight(Either.ofRight(Json.readValue(
        res.body(),
        HandlerValidationError.class
      )))
        : Either.ofRight(Either.ofLeft(Json.readValue(
        res.body(),
        HandlerGeneralError.class
      )));
    } finally {
      Errors.swallow(body::close);
    }
  }

  @SuppressWarnings("unused")
  public String getJobStatus(final String jobId) throws Exception {
    logger.trace("Handler#getJobStatus(String)");
    final var res = HttpClient.newHttpClient().send(
      HttpRequest.newBuilder(URI.create(
        format(statusEndpoint, svc.getName(), jobId)))
        .GET()
        .build(),
      BodyHandlers.ofString()
    );

    if (res.statusCode() != 200)
      throw new Exception("Unexpected response code from dataset handler");

    final var body = Format.Json.readTree(res.body());
    return body.get("status").textValue();
  }

  public static Optional < Handler > getHandler(final String format) {
    logger.trace("Handler#getHandler(String)");
    var form = format.toLowerCase();

    if (handlers.containsKey(form))
      return Optional.ofNullable(handlers.get(form));

    var optSvc = Main.jsonConfig.getServices()
      .stream()
      .filter(svc -> form.equals(svc.getDsType()))
      .findAny();

    if (optSvc.isEmpty()) {
      handlers.put(form, null);
      return Optional.empty();
    }

    var out = new Handler(optSvc.get());
    handlers.put(form, out);
    return Optional.of(out);
  }
}
