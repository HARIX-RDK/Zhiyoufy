INSERT INTO ems_environment (id, parent_id, name, worker_labels, created_by, modified_by) VALUES (
	1, NULL, 'env_001', '{"test_env_001":true}', 'builtin', 'builtin');
INSERT INTO ems_environment (id, parent_id, name, worker_labels, created_by, modified_by) VALUES (
	2, 1, 'env_001_001', '{"test_env_001":true}', 'builtin', 'builtin');

INSERT INTO ems_environment_user_relation(environment_id, user_id, is_owner) VALUES (
	1, 2, TRUE
);
INSERT INTO ems_environment_user_relation(environment_id, user_id, is_owner) VALUES (
	2, 2, TRUE
);

INSERT INTO ums_permission (id, name) VALUES (51, 'environments.create');
INSERT INTO ums_permission (id, name) VALUES (52, 'environments.delete');
INSERT INTO ums_permission (id, name) VALUES (53, 'environments.get');
INSERT INTO ums_permission (id, name) VALUES (54, 'environments.update');

INSERT INTO ums_permission (id, name) VALUES (61, 'environmentLists.create');
INSERT INTO ums_permission (id, name) VALUES (62, 'environmentLists.delete');
INSERT INTO ums_permission (id, name) VALUES (63, 'environmentLists.get');
INSERT INTO ums_permission (id, name) VALUES (64, 'environmentLists.update');

INSERT INTO ums_role (id, name) VALUES (41, 'environment.owner');
INSERT INTO ums_role (id, name) VALUES (42, 'environment.editor');
INSERT INTO ums_role (id, name) VALUES (43, 'environment.viewer');

INSERT INTO ums_role_permission_relation (role_id, permission_id) VALUES (41, 51);
INSERT INTO ums_role_permission_relation (role_id, permission_id) VALUES (41, 52);
INSERT INTO ums_role_permission_relation (role_id, permission_id) VALUES (41, 53);
INSERT INTO ums_role_permission_relation (role_id, permission_id) VALUES (41, 54);

INSERT INTO ums_role_permission_relation (role_id, permission_id) VALUES (42, 53);
INSERT INTO ums_role_permission_relation (role_id, permission_id) VALUES (42, 54);

INSERT INTO ums_role_permission_relation (role_id, permission_id) VALUES (43, 53);

INSERT INTO ums_role (id, name) VALUES (51, 'environmentList.owner');
INSERT INTO ums_role (id, name) VALUES (52, 'environmentList.editor');
INSERT INTO ums_role (id, name) VALUES (53, 'environmentList.viewer');

INSERT INTO ums_role_permission_relation (role_id, permission_id) VALUES (51, 61);
INSERT INTO ums_role_permission_relation (role_id, permission_id) VALUES (51, 62);
INSERT INTO ums_role_permission_relation (role_id, permission_id) VALUES (51, 63);
INSERT INTO ums_role_permission_relation (role_id, permission_id) VALUES (51, 64);

INSERT INTO ums_role_permission_relation (role_id, permission_id) VALUES (52, 63);
INSERT INTO ums_role_permission_relation (role_id, permission_id) VALUES (52, 64);

INSERT INTO ums_role_permission_relation (role_id, permission_id) VALUES (53, 63);

-- user: admin
INSERT INTO ums_user_role_relation (user_id, role_id) VALUES (2, 41);
INSERT INTO ums_user_role_relation (user_id, role_id) VALUES (2, 51);

-- user: set001
INSERT INTO ums_user_role_relation (user_id, role_id) VALUES (3, 41);
INSERT INTO ums_user_role_relation (user_id, role_id) VALUES (3, 51);

-- user: swe001
INSERT INTO ums_user_role_relation (user_id, role_id) VALUES (4, 53);

-- user: devops001
INSERT INTO ums_user_role_relation (user_id, role_id) VALUES (5, 53);