INSERT INTO jms_job_schedule (id, name, project_id,project_name,worker_app_id, worker_app_name, worker_group_id,
                                 worker_group_name, environment_id,environment_name,template_id,
                                 template_name,run_tag, run_num,parallel_num,include_tags,exclude_tags,
                                 add_tags,remove_tags,crontab_config,created_time,
                                 created_by,modified_time,modified_by ) VALUES (1, '111222333', 1, 'proj_1',
                                 1, 'worker_app_name_1', 1, 'worker_group_name_1',3,'env_1', 2, 'harix_test',
                                 'for_test',1, 1, '','','','','0 2 3 * * *', '2022-09-14T07:30:01.027+00:00','stella',
                                 '2022-09-14T07:30:01.027+00:00', 'stella');