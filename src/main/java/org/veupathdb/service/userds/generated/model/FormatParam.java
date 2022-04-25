package org.veupathdb.service.userds.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
    as = FormatParamImpl.class
)
public interface FormatParam {
  @JsonProperty("key")
  String getKey();

  @JsonProperty("key")
  void setKey(String key);

  @JsonProperty("value")
  String getValue();

  @JsonProperty("value")
  void setValue(String value);
}
