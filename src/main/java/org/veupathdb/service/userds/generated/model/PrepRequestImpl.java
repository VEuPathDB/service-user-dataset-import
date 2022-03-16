package org.veupathdb.service.userds.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "datasetName",
    "datasetType",
    "description",
    "summary",
    "projects",
    "origin"
})
public class PrepRequestImpl implements PrepRequest {
  @JsonProperty("datasetName")
  private String datasetName;

  @JsonProperty("datasetType")
  private String datasetType;

  @JsonProperty("description")
  private String description;

  @JsonProperty("summary")
  private String summary;

  @JsonProperty("projects")
  private List<String> projects;

  @JsonProperty("origin")
  private DatasetOrigin origin;

  @JsonProperty("datasetName")
  public String getDatasetName() {
    return this.datasetName;
  }

  @JsonProperty("datasetName")
  public void setDatasetName(String datasetName) {
    this.datasetName = datasetName;
  }

  @JsonProperty("datasetType")
  public String getDatasetType() {
    return this.datasetType;
  }

  @JsonProperty("datasetType")
  public void setDatasetType(String datasetType) {
    this.datasetType = datasetType;
  }

  @JsonProperty("description")
  public String getDescription() {
    return this.description;
  }

  @JsonProperty("description")
  public void setDescription(String description) {
    this.description = description;
  }

  @JsonProperty("summary")
  public String getSummary() {
    return this.summary;
  }

  @JsonProperty("summary")
  public void setSummary(String summary) {
    this.summary = summary;
  }

  @JsonProperty("projects")
  public List<String> getProjects() {
    return this.projects;
  }

  @JsonProperty("projects")
  public void setProjects(List<String> projects) {
    this.projects = projects;
  }

  @JsonProperty("origin")
  public DatasetOrigin getOrigin() {
    return this.origin;
  }

  @JsonProperty("origin")
  public void setOrigin(DatasetOrigin origin) {
    this.origin = origin;
  }
}
