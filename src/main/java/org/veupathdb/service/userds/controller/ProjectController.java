package org.veupathdb.service.userds.controller;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import jakarta.ws.rs.NotFoundException;
import org.veupathdb.service.userds.Main;
import org.veupathdb.service.userds.generated.resources.Projects;
import org.veupathdb.service.userds.model.Service;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;

public class ProjectController implements Projects {
  private static final String[] BASE_FILE_TYPES = {"zip", "gz", "tgz"};

  @Override
  public GetProjectsResponse getProjects() {
    return GetProjectsResponse.respond200WithApplicationJson(
      Main.jsonConfig.getServices().stream()
        .map(Service::getProjects)
        .flatMap(Arrays::stream)
        .distinct()
        .sorted()
        .collect(Collectors.toList()));
  }

  @Override
  public GetProjectsDatasetTypesByProjectResponse getProjectsDatasetTypesByProject(String project) {
    final var svcs = Main.jsonConfig.getServices()
      .stream()
      .filter(svc -> asList(svc.getProjects()).contains(project))
      .toArray(Service[]::new);

    if (svcs.length == 0) {
      throw new NotFoundException("no configured handlers for " + project);
    }

    return GetProjectsDatasetTypesByProjectResponse
      .respond200WithApplicationJson((stream(svcs)
        .map(Service::getDsType)
        .sorted()
        .distinct()
        .collect(Collectors.toList())));
  }

  @Override
  public GetProjectsDatasetTypesFileTypesByProjectAndDsTypeResponse getProjectsDatasetTypesFileTypesByProjectAndDsType(
    String project,
    String dsType
  ) {
    final var svcs = Main.jsonConfig.getServices()
      .stream()
      .filter(svc -> dsType.equals(svc.getDsType()))
      .filter(svc -> asList(svc.getProjects()).contains(project))
      .toArray(Service[]::new);

    if (svcs.length == 0) {
      throw new NotFoundException(format("dataset type %s not found for project type %s", dsType, project));
    }

    return GetProjectsDatasetTypesFileTypesByProjectAndDsTypeResponse
      .respond200WithApplicationJson(Stream.concat(stream(svcs)
        .map(Service::getFileTypes)
        .flatMap(Arrays::stream), Stream.of(BASE_FILE_TYPES))
        .sorted()
        .distinct()
        .collect(Collectors.toList()));
  }
}
