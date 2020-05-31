package org.veupathdb.service.userds.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;

@JsonDeserialize(
    as = PrepRequestImpl.class
)
public interface PrepRequest {
  @JsonProperty("datasetName")
  String getDatasetName();

  @JsonProperty("datasetName")
  void setDatasetName(String datasetName);

  @JsonProperty("description")
  String getDescription();

  @JsonProperty("description")
  void setDescription(String description);

  @JsonProperty("summary")
  String getSummary();

  @JsonProperty("summary")
  void setSummary(String summary);

  @JsonProperty("projects")
  List<String> getProjects();

  @JsonProperty("projects")
  void setProjects(List<String> projects);

  @JsonProperty("datasetType")
  String getDatasetType();

  @JsonProperty("datasetType")
  void setDatasetType(String dsType);
}
