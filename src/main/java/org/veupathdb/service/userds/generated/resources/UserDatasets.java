package org.veupathdb.service.userds.generated.resources;

import java.io.InputStream;
import java.util.List;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.GenericEntity;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.veupathdb.service.userds.generated.model.PrepRequest;
import org.veupathdb.service.userds.generated.model.PrepResponse;
import org.veupathdb.service.userds.generated.model.ProcessResponse;
import org.veupathdb.service.userds.generated.model.StatusResponse;
import org.veupathdb.service.userds.generated.model.UserDatasetsJobIdPostMultipartFormData;
import org.veupathdb.service.userds.generated.support.ResponseDelegate;

@Path("/user-datasets")
public interface UserDatasets {
  @GET
  @Produces("application/json")
  GetUserDatasetsResponse getUserDatasets(@QueryParam("limit") @DefaultValue("100") Integer limit,
      @QueryParam("page") @DefaultValue("0") Integer page);

  @POST
  @Produces("application/json")
  @Consumes("application/json")
  PostUserDatasetsResponse postUserDatasets(PrepRequest entity);

  @DELETE
  @Path("/{jobId}")
  DeleteUserDatasetsByJobIdResponse deleteUserDatasetsByJobId(@PathParam("jobId") String jobId);

  @GET
  @Path("/{jobId}")
  @Produces("application/json")
  GetUserDatasetsByJobIdResponse getUserDatasetsByJobId(@PathParam("jobId") String jobId);

  @POST
  @Path("/{jobId}")
  @Produces("application/json")
  @Consumes("multipart/form-data")
  public PostUserDatasetsByJobIdResponse postUserDatasetsByJobId(
      @PathParam("jobId") String jobId,
      @FormDataParam("uploadMethod") String uploadMethod,
      @FormDataParam("file") InputStream file,
      @FormDataParam("file") FormDataContentDisposition meta,
      @FormDataParam("url") String url
  );

  class GetUserDatasetsResponse extends ResponseDelegate {
    private GetUserDatasetsResponse(Response response, Object entity) {
      super(response, entity);
    }

    private GetUserDatasetsResponse(Response response) {
      super(response);
    }

    public static GetUserDatasetsResponse respond200WithApplicationJson(
        List<StatusResponse> entity) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
      GenericEntity<List<StatusResponse>> wrappedEntity = new GenericEntity<List<StatusResponse>>(entity){};
      responseBuilder.entity(wrappedEntity);
      return new GetUserDatasetsResponse(responseBuilder.build(), wrappedEntity);
    }
  }

  class PostUserDatasetsResponse extends ResponseDelegate {
    private PostUserDatasetsResponse(Response response, Object entity) {
      super(response, entity);
    }

    private PostUserDatasetsResponse(Response response) {
      super(response);
    }

    public static PostUserDatasetsResponse respond200WithApplicationJson(PrepResponse entity) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostUserDatasetsResponse(responseBuilder.build(), entity);
    }
  }

  class DeleteUserDatasetsByJobIdResponse extends ResponseDelegate {
    private DeleteUserDatasetsByJobIdResponse(Response response, Object entity) {
      super(response, entity);
    }

    private DeleteUserDatasetsByJobIdResponse(Response response) {
      super(response);
    }

    public static DeleteUserDatasetsByJobIdResponse respond204() {
      Response.ResponseBuilder responseBuilder = Response.status(204);
      return new DeleteUserDatasetsByJobIdResponse(responseBuilder.build());
    }
  }

  class GetUserDatasetsByJobIdResponse extends ResponseDelegate {
    private GetUserDatasetsByJobIdResponse(Response response, Object entity) {
      super(response, entity);
    }

    private GetUserDatasetsByJobIdResponse(Response response) {
      super(response);
    }

    public static GetUserDatasetsByJobIdResponse respond200WithApplicationJson(
        StatusResponse entity) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetUserDatasetsByJobIdResponse(responseBuilder.build(), entity);
    }
  }

  class PostUserDatasetsByJobIdResponse extends ResponseDelegate {
    private PostUserDatasetsByJobIdResponse(Response response, Object entity) {
      super(response, entity);
    }

    private PostUserDatasetsByJobIdResponse(Response response) {
      super(response);
    }

    public static PostUserDatasetsByJobIdResponse respond200WithApplicationJson(
        ProcessResponse entity) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostUserDatasetsByJobIdResponse(responseBuilder.build(), entity);
    }
  }
}
