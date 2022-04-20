package org.veupathdb.service.userds.model.handler;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class HandlerPayload
{
  private final String name;

  private final long userId;

  private final List < String > projects;

  private final String summary;

  private final String description;

  private final DatasetOrigin origin;

  private final Map< String, String > handlerParams;

  public HandlerPayload(
    String name,
    long userId,
    List < String > projects,
    DatasetOrigin origin,
    String description,
    String summary,
    Map < String, String > handlerParams
  ) {
    this.name = name;
    this.userId = userId;
    this.projects = projects;
    this.origin = origin;
    this.description = description;
    this.summary = summary;
    this.handlerParams = handlerParams;
  }

  @JsonGetter("name")
  public String getName() {
    return name;
  }

  @JsonGetter("owner")
  public long getUserId() {
    return userId;
  }

  @JsonGetter("projects")
  public List < String > getProjects() {
    return projects;
  }

  @JsonGetter
  public String getSummary() {
    return summary;
  }

  @JsonGetter
  public String getDescription() {
    return description;
  }

  @JsonGetter
  public DatasetOrigin getOrigin() {
    return origin;
  }
}
