INSERT INTO ums_permission (id, name) VALUES (81, 'workerApps.create');
INSERT INTO ums_permission (id, name) VALUES (82, 'workerApps.delete');
INSERT INTO ums_permission (id, name) VALUES (83, 'workerApps.get');
INSERT INTO ums_permission (id, name) VALUES (84, 'workerApps.update');

INSERT INTO ums_role (id, name) VALUES (81, 'workerApp.owner');
INSERT INTO ums_role (id, name) VALUES (82, 'workerApp.editor');
INSERT INTO ums_role (id, name) VALUES (83, 'workerApp.viewer');

INSERT INTO ums_role_permission_relation (role_id, permission_id) VALUES (81, 81);
INSERT INTO ums_role_permission_relation (role_id, permission_id) VALUES (81, 82);
INSERT INTO ums_role_permission_relation (role_id, permission_id) VALUES (81, 83);
INSERT INTO ums_role_permission_relation (role_id, permission_id) VALUES (81, 84);

INSERT INTO ums_role_permission_relation (role_id, permission_id) VALUES (82, 83);
INSERT INTO ums_role_permission_relation (role_id, permission_id) VALUES (82, 84);

INSERT INTO ums_role_permission_relation (role_id, permission_id) VALUES (83, 83);

-- Test Data

INSERT INTO ums_user_role_relation (user_id, role_id) VALUES (2, 81);

INSERT INTO ums_user_role_relation (user_id, role_id) VALUES (3, 82);

INSERT INTO wms_worker_app (id, name, created_by, modified_by) VALUES (
	1, 'WorkerApp_001', 'builtin', 'builtin');

INSERT INTO wms_worker_app_user_relation(worker_app_id, user_id, is_owner) VALUES (
	1, 2, TRUE
);
INSERT INTO wms_worker_app_user_relation(worker_app_id, user_id, is_editor) VALUES (
	1, 3, TRUE
);

INSERT INTO wms_worker_group (id, worker_app_id, name, worker_labels, created_by, modified_by)
	VALUES (1, 1, 'WorkerGroup_001', '{"test_env_configs":true}', 'spring-test', 'spring-test');

INSERT INTO wms_group_token(id, worker_app_id, worker_group_id,
	name, secret, created_by, modified_by) VALUES (
	1, 1, 1, 'GroupToken_001', 'hard-secret', 'spring-test', 'spring-test'
);
