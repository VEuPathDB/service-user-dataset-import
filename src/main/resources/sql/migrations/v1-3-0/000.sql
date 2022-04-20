ALTER TABLE
    ds_jobs.jobs
ADD COLUMN IF NOT EXISTS
    params JSONB
;
