package org.veupathdb.service.userds.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

@JsonDeserialize(
    as = StatusResponseImpl.class
)
public interface StatusResponse {
  @JsonProperty("id")
  String getId();

  @JsonProperty("id")
  void setId(String id);

  @JsonProperty("datasetName")
  String getDatasetName();

  @JsonProperty("datasetName")
  void setDatasetName(String datasetName);

  @JsonProperty("description")
  String getDescription();

  @JsonProperty("description")
  void setDescription(String description);

  @JsonProperty("summary")
  String getSummary();

  @JsonProperty("summary")
  void setSummary(String summary);

  @JsonProperty("datasetId")
  Integer getDatasetId();

  @JsonProperty("datasetId")
  void setDatasetId(Integer datasetId);

  @JsonProperty("projects")
  List<String> getProjects();

  @JsonProperty("projects")
  void setProjects(List<String> projects);

  @JsonProperty("status")
  JobStatus getStatus();

  @JsonProperty("status")
  void setStatus(JobStatus status);

  @JsonProperty("statusDetails")
  StatusDetailsType getStatusDetails();

  @JsonProperty("statusDetails")
  void setStatusDetails(StatusDetailsType statusDetails);

  @JsonProperty("origin")
  DatasetOrigin getOrigin();

  @JsonProperty("origin")
  void setOrigin(DatasetOrigin origin);

  @JsonProperty("formatParams")
  List<FormatParam> getFormatParams();

  @JsonProperty("formatParams")
  void setFormatParams(List<FormatParam> formatParams);

  @JsonProperty("started")
  Date getStarted();

  @JsonProperty("started")
  void setStarted(Date started);

  @JsonProperty("finished")
  Date getFinished();

  @JsonProperty("finished")
  void setFinished(Date finished);

  @JsonDeserialize(
      using = StatusDetailsType.StatusDetailsDeserializer.class
  )
  @JsonSerialize(
      using = StatusDetailsType.Serializer.class
  )
  interface StatusDetailsType {
    ValidationErrors getValidationErrors();

    Boolean isValidationErrors();

    JobError getJobError();

    Boolean isJobError();

    class Serializer extends StdSerializer<StatusDetailsType> {
      public Serializer() {
        super(StatusDetailsType.class);}

      public void serialize(StatusDetailsType object, JsonGenerator jsonGenerator,
          SerializerProvider jsonSerializerProvider) throws IOException, JsonProcessingException {
        if ( object.isValidationErrors()) {
          jsonGenerator.writeObject(object.getValidationErrors());
          return;
        }
        if ( object.isJobError()) {
          jsonGenerator.writeObject(object.getJobError());
          return;
        }
        throw new IOException("Can't figure out type of object" + object);
      }
    }

    class StatusDetailsDeserializer extends StdDeserializer<StatusDetailsType> {
      public StatusDetailsDeserializer() {
        super(StatusDetailsType.class);}

      private Boolean looksLikeValidationErrors(Map<String, Object> map) {
        return map.keySet().containsAll(Arrays.asList("errors"));
      }

      private Boolean looksLikeJobError(Map<String, Object> map) {
        return map.keySet().containsAll(Arrays.asList("message"));
      }

      public StatusDetailsType deserialize(JsonParser jsonParser,
          DeserializationContext jsonContext) throws IOException, JsonProcessingException {
        ObjectMapper mapper  = new ObjectMapper();
        Map<String, Object> map = mapper.readValue(jsonParser, Map.class);
        if ( looksLikeValidationErrors(map) ) return new StatusResponseImpl.StatusDetailsTypeImpl(mapper.convertValue(map, ValidationErrorsImpl.class));
        if ( looksLikeJobError(map) ) return new StatusResponseImpl.StatusDetailsTypeImpl(mapper.convertValue(map, JobErrorImpl.class));
        throw new IOException("Can't figure out type of object" + map);
      }
    }
  }
}
