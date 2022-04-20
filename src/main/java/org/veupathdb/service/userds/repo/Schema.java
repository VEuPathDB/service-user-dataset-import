package org.veupathdb.service.userds.repo;

public interface Schema
{
  interface Table
  {
    interface Jobs
    {
      String
        DB_ID          = "db_id",
        JOB_ID         = "job_id",
        USER_ID        = "user_id",
        STATUS         = "status_id",
        NAME           = "name",
        DESCRIPTION    = "description",
        SUMMARY        = "summary",
        ORIGIN_ID      = "origin_id",
        HANDLER_PARAMS = "handler_params",
        TYPE           = "type",
        STARTED        = "started",
        FINISHED       = "finished";
    }

    interface JobIrodsIDs
    {
      String
        DB_ID    = "db_id",
        IRODS_ID = "irods_id";
    }

    interface JobMessages
    {
      String
        DB_ID   = "db_id",
        MESSAGE = "message";
    }

  }

  interface Column
  {
    String PROJECTS = "projects";
  }
}
