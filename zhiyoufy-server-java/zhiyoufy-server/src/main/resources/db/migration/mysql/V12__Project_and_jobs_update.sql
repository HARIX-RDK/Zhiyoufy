ALTER TABLE pms_job_template ADD is_perf BOOLEAN DEFAULT FALSE;
ALTER TABLE pms_job_template ADD dashboard_addr VARCHAR(256) NOT NULL DEFAULT "";