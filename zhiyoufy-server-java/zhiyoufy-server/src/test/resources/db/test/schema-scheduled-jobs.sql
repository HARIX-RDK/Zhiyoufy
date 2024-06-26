CREATE TABLE jms_job_schedule (
   id BIGINT NOT NULL AUTO_INCREMENT primary key,
   name VARCHAR(128) NOT NULL,
   project_id BIGINT NOT NULL,
   project_name VARCHAR(64) NOT NULL,
   worker_app_id BIGINT NOT NULL,
   worker_app_name VARCHAR(64) NOT NULL,
   worker_group_id BIGINT NOT NULL,
   worker_group_name VARCHAR(128) NOT NULL,
   environment_id BIGINT NOT NULL,
   environment_name VARCHAR(64) NOT NULL,
   template_id BIGINT NOT NULL,
   template_name VARCHAR(128) NOT NULL,

   run_tag VARCHAR(64) NOT NULL,
   run_num INT NOT NULL,
   parallel_num INT NOT NULL,
   include_tags VARCHAR(512),
   exclude_tags VARCHAR(512),
   add_tags VARCHAR(512),
   remove_tags VARCHAR(512),
   crontab_config VARCHAR(512),

   created_time datetime(3) DEFAULT CURRENT_TIMESTAMP(3),
   created_by VARCHAR(32) NOT NULL,

   modified_time datetime(3) DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
   modified_by VARCHAR(32) NOT NULL,

   UNIQUE (name)

);