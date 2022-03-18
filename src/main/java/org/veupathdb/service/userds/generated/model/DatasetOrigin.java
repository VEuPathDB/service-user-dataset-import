package org.veupathdb.service.userds.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum DatasetOrigin {
  @JsonProperty("galaxy")
  GALAXY("galaxy"),

  @JsonProperty("direct-upload")
  DIRECTUPLOAD("direct-upload");

  private String name;

  DatasetOrigin(String name) {
    this.name = name;
  }

  public String getValue() {
    return name;
  }
}
