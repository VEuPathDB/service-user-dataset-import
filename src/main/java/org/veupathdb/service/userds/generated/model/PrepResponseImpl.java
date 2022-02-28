package org.veupathdb.service.userds.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "status",
    "jobId"
})
public class PrepResponseImpl implements PrepResponse {
  @JsonProperty("status")
  private PrepResponse.StatusType status;

  @JsonProperty("jobId")
  private String jobId;

  @JsonProperty("status")
  public PrepResponse.StatusType getStatus() {
    return this.status;
  }

  @JsonProperty("status")
  public void setStatus(PrepResponse.StatusType status) {
    this.status = status;
  }

  @JsonProperty("jobId")
  public String getJobId() {
    return this.jobId;
  }

  @JsonProperty("jobId")
  public void setJobId(String jobId) {
    this.jobId = jobId;
  }
}
