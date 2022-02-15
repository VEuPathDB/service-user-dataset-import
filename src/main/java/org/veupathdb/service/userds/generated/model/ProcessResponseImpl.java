package org.veupathdb.service.userds.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("status")
public class ProcessResponseImpl implements ProcessResponse {
  @JsonProperty(
      value = "status",
      defaultValue = "ok"
  )
  private ProcessResponse.StatusType status;

  @JsonProperty(
      value = "status",
      defaultValue = "ok"
  )
  public ProcessResponse.StatusType getStatus() {
    return this.status;
  }

  @JsonProperty(
      value = "status",
      defaultValue = "ok"
  )
  public void setStatus(ProcessResponse.StatusType status) {
    this.status = status;
  }
}
