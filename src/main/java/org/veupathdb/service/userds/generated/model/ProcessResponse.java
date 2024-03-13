package org.veupathdb.service.userds.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = ProcessResponseImpl.class
)
public interface ProcessResponse {
  @JsonProperty(
      value = "status",
      defaultValue = "ok"
  )
  StatusType getStatus();

  @JsonProperty(
      value = "status",
      defaultValue = "ok"
  )
  void setStatus(StatusType status);

  enum StatusType {
    @JsonProperty("ok")
    OK("ok");

    public final String value;

    public String getValue() {
      return this.value;
    }

    StatusType(String name) {
      this.value = name;
    }
  }
}
