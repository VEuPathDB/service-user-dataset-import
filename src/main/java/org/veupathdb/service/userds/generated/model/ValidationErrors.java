package org.veupathdb.service.userds.generated.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.Map;

@JsonDeserialize(
    as = ValidationErrorsImpl.class
)
public interface ValidationErrors {
  @JsonProperty("errors")
  ErrorsType getErrors();

  @JsonProperty("errors")
  void setErrors(ErrorsType errors);

  @JsonAnyGetter
  Map<String, Object> getAdditionalProperties();

  @JsonAnySetter
  void setAdditionalProperties(String key, Object value);

  @JsonDeserialize(
      as = ValidationErrorsImpl.ErrorsTypeImpl.class
  )
  interface ErrorsType {
    @JsonProperty("general")
    List<String> getGeneral();

    @JsonProperty("general")
    void setGeneral(List<String> general);

    @JsonProperty("byKey")
    ByKeyType getByKey();

    @JsonProperty("byKey")
    void setByKey(ByKeyType byKey);

    @JsonDeserialize(
        as = ValidationErrorsImpl.ErrorsTypeImpl.ByKeyTypeImpl.class
    )
    interface ByKeyType {
      @JsonAnyGetter
      Map<String, Object> getAdditionalProperties();

      @JsonAnySetter
      void setAdditionalProperties(String key, Object value);
    }
  }
}
