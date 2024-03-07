package org.veupathdb.service.userds.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum DatasetOrigin {
  @JsonProperty("galaxy")
  GALAXY("galaxy"),

  @JsonProperty("direct-upload")
  DIRECTUPLOAD("direct-upload");

  public final String value;

  public String getValue() {
    return this.value;
  }

  DatasetOrigin(String name) {
    this.value = name;
  }
}
