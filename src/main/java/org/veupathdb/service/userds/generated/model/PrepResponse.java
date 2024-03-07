package org.veupathdb.service.userds.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = PrepResponseImpl.class
)
public interface PrepResponse {
  @JsonProperty("status")
  StatusType getStatus();

  @JsonProperty("status")
  void setStatus(StatusType status);

  @JsonProperty("jobId")
  String getJobId();

  @JsonProperty("jobId")
  void setJobId(String jobId);

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
