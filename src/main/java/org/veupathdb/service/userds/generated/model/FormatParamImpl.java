package org.veupathdb.service.userds.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "key",
    "value"
})
public class FormatParamImpl implements FormatParam {
  @JsonProperty("key")
  private String key;

  @JsonProperty("value")
  private String value;

  @JsonProperty("key")
  public String getKey() {
    return this.key;
  }

  @JsonProperty("key")
  public void setKey(String key) {
    this.key = key;
  }

  @JsonProperty("value")
  public String getValue() {
    return this.value;
  }

  @JsonProperty("value")
  public void setValue(String value) {
    this.value = value;
  }
}
