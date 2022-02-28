package org.veupathdb.service.userds.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum JobStatus {
  @JsonProperty("sending-to-handler")
  SENDINGTOHANDLER("sending-to-handler"),

  @JsonProperty("handler-unpacking")
  HANDLERUNPACKING("handler-unpacking"),

  @JsonProperty("handler-processing")
  HANDLERPROCESSING("handler-processing"),

  @JsonProperty("handler-packing")
  HANDLERPACKING("handler-packing"),

  @JsonProperty("sending-to-datastore")
  SENDINGTODATASTORE("sending-to-datastore"),

  @JsonProperty("success")
  SUCCESS("success"),

  @JsonProperty("rejected")
  REJECTED("rejected"),

  @JsonProperty("errored")
  ERRORED("errored");

  private String name;

  JobStatus(String name) {
    this.name = name;
  }
  public String getValue(){ return name; } 
}
