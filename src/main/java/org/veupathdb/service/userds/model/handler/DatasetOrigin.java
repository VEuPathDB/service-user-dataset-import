package org.veupathdb.service.userds.model.handler;

import java.util.Optional;

public enum DatasetOrigin
{
  DIRECT_UPLOAD("direct-upload"),
  GALAXY("galaxy");

  private final String value;

  DatasetOrigin(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public static Optional < DatasetOrigin > fromString(String value) {
    for (var val : values())
      if (val.value.equals(value))
        return Optional.of(val);

    return Optional.empty();
  }

  public static DatasetOrigin fromApiOrigin(org.veupathdb.service.userds.generated.model.DatasetOrigin datasetOrigin) {
    return fromString(datasetOrigin.getValue())
        .orElseThrow(() -> new IllegalStateException(
            "Internal API DatasetOrigin enum does not match external API DatasetOrigin enum [missing " + datasetOrigin + "]"));
  }

  public org.veupathdb.service.userds.generated.model.DatasetOrigin toApiOrigin() {
    for (org.veupathdb.service.userds.generated.model.DatasetOrigin origin :
        org.veupathdb.service.userds.generated.model.DatasetOrigin.values()) {
      if (origin.getValue().equals(value)) {
        return origin;
      }
    }
    throw new IllegalStateException("External API DatasetOrigin enum does not match internal DatasetOrigin enum [missing " + value + "]");
  }
}
