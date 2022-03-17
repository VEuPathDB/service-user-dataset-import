package org.veupathdb.service.userds.generated.resources;

import java.util.List;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.GenericEntity;
import jakarta.ws.rs.core.Response;
import org.veupathdb.service.userds.generated.support.ResponseDelegate;

@Path("/projects")
public interface Projects {
  @GET
  @Produces("application/json")
  GetProjectsResponse getProjects();

  @GET
  @Path("/{project}/datasetTypes")
  @Produces("application/json")
  GetProjectsDatasetTypesByProjectResponse getProjectsDatasetTypesByProject(
      @PathParam("project") String project);

  @GET
  @Path("/{project}/datasetTypes/{dsType}/fileTypes")
  @Produces("application/json")
  GetProjectsDatasetTypesFileTypesByProjectAndDsTypeResponse getProjectsDatasetTypesFileTypesByProjectAndDsType(
      @PathParam("project") String project, @PathParam("dsType") String dsType);

  class GetProjectsResponse extends ResponseDelegate {
    private GetProjectsResponse(Response response, Object entity) {
      super(response, entity);
    }

    private GetProjectsResponse(Response response) {
      super(response);
    }

    public static GetProjectsResponse respond200WithApplicationJson(List<String> entity) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
      GenericEntity<List<String>> wrappedEntity = new GenericEntity<List<String>>(entity){};
      responseBuilder.entity(wrappedEntity);
      return new GetProjectsResponse(responseBuilder.build(), wrappedEntity);
    }
  }

  class GetProjectsDatasetTypesByProjectResponse extends ResponseDelegate {
    private GetProjectsDatasetTypesByProjectResponse(Response response, Object entity) {
      super(response, entity);
    }

    private GetProjectsDatasetTypesByProjectResponse(Response response) {
      super(response);
    }

    public static GetProjectsDatasetTypesByProjectResponse respond200WithApplicationJson(
        List<Object> entity) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
      GenericEntity<List<Object>> wrappedEntity = new GenericEntity<List<Object>>(entity){};
      responseBuilder.entity(wrappedEntity);
      return new GetProjectsDatasetTypesByProjectResponse(responseBuilder.build(), wrappedEntity);
    }
  }

  class GetProjectsDatasetTypesFileTypesByProjectAndDsTypeResponse extends ResponseDelegate {
    private GetProjectsDatasetTypesFileTypesByProjectAndDsTypeResponse(Response response,
        Object entity) {
      super(response, entity);
    }

    private GetProjectsDatasetTypesFileTypesByProjectAndDsTypeResponse(Response response) {
      super(response);
    }

    public static GetProjectsDatasetTypesFileTypesByProjectAndDsTypeResponse respond200WithApplicationJson(
        List<Object> entity) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
      GenericEntity<List<Object>> wrappedEntity = new GenericEntity<List<Object>>(entity){};
      responseBuilder.entity(wrappedEntity);
      return new GetProjectsDatasetTypesFileTypesByProjectAndDsTypeResponse(responseBuilder.build(), wrappedEntity);
    }
  }
}
