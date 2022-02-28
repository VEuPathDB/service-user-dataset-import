package org.veupathdb.service.userds.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum DatasetType {
  @JsonProperty("biom")
  BIOM("biom");

  private String name;

  DatasetType(String name) {
    this.name = name;
  }
  public String getValue(){ return name; } 
}
