package org.veupathdb.service.userds.generated.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.io.File;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "uploadMethod",
    "url",
    "file"
})
public class UserDatasetsJobIdPostMultipartFormDataImpl implements UserDatasetsJobIdPostMultipartFormData {
  @JsonProperty("uploadMethod")
  private UserDatasetsJobIdPostMultipartFormData.UploadMethodType uploadMethod;

  @JsonProperty("url")
  private String url;

  @JsonProperty("file")
  private File file;

  @JsonIgnore
  private Map<String, Object> additionalProperties = new ExcludingMap();

  @JsonProperty("uploadMethod")
  public UserDatasetsJobIdPostMultipartFormData.UploadMethodType getUploadMethod() {
    return this.uploadMethod;
  }

  @JsonProperty("uploadMethod")
  public void setUploadMethod(
      UserDatasetsJobIdPostMultipartFormData.UploadMethodType uploadMethod) {
    this.uploadMethod = uploadMethod;
  }

  @JsonProperty("url")
  public String getUrl() {
    return this.url;
  }

  @JsonProperty("url")
  public void setUrl(String url) {
    this.url = url;
  }

  @JsonProperty("file")
  public File getFile() {
    return this.file;
  }

  @JsonProperty("file")
  public void setFile(File file) {
    this.file = file;
  }

  @JsonAnyGetter
  public Map<String, Object> getAdditionalProperties() {
    return additionalProperties;
  }

  @JsonAnySetter
  public void setAdditionalProperties(String key, Object value) {
    this.additionalProperties.put(key, value);
  }
}
