WITH job AS (
  INSERT INTO
    ds_jobs.jobs (
      job_id
    , user_id
    , status_id
    , name
    , description
    , summary
    , origin_id
    , type
    , params
  )
  VALUES
    (?, ?, ?, ?, ?, ?, ?, ?, ?::JSONB)
  RETURNING db_id
)
INSERT INTO
  ds_jobs.job_projects (db_id, project_id)
SELECT
  (SELECT db_id FROM job)
, tmp.project
FROM
  (SELECT UNNEST(?::SMALLINT[]) AS project) AS tmp
;
