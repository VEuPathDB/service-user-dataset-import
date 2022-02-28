package org.veupathdb.service.userds.model;

import java.util.Arrays;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonValue;

public enum JobStatus
{
  /*------------------------------------------------------------------------*\
  |                                                                          |
  | WARNING                                                                  |
  |                                                                          |
  | This enum is coupled directly with the contents of the ds_type.status    |
  | table in the job meta postgres database.                                 |
  |                                                                          |
  | When adding to this enum:                                                |
  | 1. Add a corresponding entry to the END of the status insert query in    |
  |    the DDL for the database in the repo for the dataset importer         |
  |    container stack (at the time of this comment it is located at         |
  |    stack-user-dataset-import/blob/master/init.sql).                      |
  | 2. Run an insert against the running postgres meta database inserting    |
  |    your new status (Ex: INSERT INTO ds_types.status (name) VALUES (?);)  |
  |                                                                          |
  | When removing from this enum:                                            |
  | 1. Stop and rethink your life choices.  What led you here?               |
  | 2. Determine what the database ID of the status to remove is.            |
  | 3. Update all rows in the job meta database with the old status to point |
  |    a new status of your choice (hopefully one that makes sense).         |
  | 4. Delete the row in the job meta ds_type.status table for the status    |
  |    you are removing.                                                     |
  | 5. Remove the corresponding line in the job meta database DDL for the    |
  |    status to be removed.                                                 |
  |                                                                          |
  \*------------------------------------------------------------------------*/

  AWAITING_UPLOAD("awaiting-upload"),
  SENDING_TO_HANDLER("sending-to-handler"),
  HANDLER_UNPACKING("handler-unpacking"),
  HANDLER_PROCESSING("handler-processing"),
  HANDLER_PACKING("handler-packing"),
  RECEIVING_FROM_HANDLER("receiving-from-handler"),
  SENDING_TO_DATASTORE("sending-to-datastore"),
  DATASTORE_UNPACKING("datastore-unpacking"),
  SUCCESS("success"),
  REJECTED("rejected"),
  ERRORED("errored");

  private final String name;

  JobStatus(String name) {
    this.name = name;
  }

  @JsonValue
  public String getName() {
    return name;
  }

  public static Optional < JobStatus > fromString(String name) {
    return Arrays.stream(values())
      .filter(v -> v.name.equals(name))
      .findAny();
  }

  public org.veupathdb.service.userds.generated.model.JobStatus toApiStatus() {
    for (org.veupathdb.service.userds.generated.model.JobStatus status :
        org.veupathdb.service.userds.generated.model.JobStatus.values()) {
      if (status.getValue().equals(name)) {
        return status;
      }
    }
    throw new IllegalStateException("External API JobStatus enum does not match internal JobStatus enum [missing " + name + "]");
  }
}
