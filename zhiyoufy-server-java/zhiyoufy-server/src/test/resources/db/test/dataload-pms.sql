INSERT INTO pms_project (id, name, created_by, modified_by) VALUES (
	1, 'project_001', 'builtin', 'builtin');

INSERT INTO pms_project_user_relation(project_id, user_id, is_owner) VALUES (
	1, 2, TRUE
);
INSERT INTO pms_project_user_relation(project_id, user_id, is_editor) VALUES (
	1, 3, TRUE
);

INSERT INTO pms_job_folder (id, project_id, parent_id,
	name, description, created_by, modified_by) VALUES (
	1, 1, 0, 'JobFolder_001', 'Level 1 folder', 'builtin', 'builtin'
);
INSERT INTO pms_job_folder (id, project_id, parent_id,
	name, description, created_by, modified_by) VALUES (
	2, 1, 1, 'JobFolder_001_001', 'Level 2 folder', 'builtin', 'builtin'
);

INSERT INTO pms_job_template (id, project_id, folder_id,
	name, description, job_path, timeout_seconds, created_by, modified_by,
	config_singles, config_collections) VALUES (
	1, 1, 1, 'JobTemplate_001', 'Template in Folder JobFolder_001',
	'job_path', 1800, 'builtin', 'builtin',
	'ConfigSingle_002, ConfigSingle_003, ConfigSingle_004',
	'ConfigCollection_010, ConfigCollection_011'
);
INSERT INTO pms_job_template (id, project_id, folder_id,
	name, description, job_path, timeout_seconds, created_by, modified_by) VALUES (
	2, 1, 2, 'JobTemplate_001_001', 'Template in Folder JobFolder_001_001',
	'job_path', 1800, 'builtin', 'builtin'
);

INSERT INTO ums_permission (id, name) VALUES (71, 'projects.create');
INSERT INTO ums_permission (id, name) VALUES (72, 'projects.delete');
INSERT INTO ums_permission (id, name) VALUES (73, 'projects.get');
INSERT INTO ums_permission (id, name) VALUES (74, 'projects.update');

INSERT INTO ums_role (id, name) VALUES (71, 'project.owner');
INSERT INTO ums_role (id, name) VALUES (72, 'project.editor');
INSERT INTO ums_role (id, name) VALUES (73, 'project.viewer');

INSERT INTO ums_role_permission_relation (role_id, permission_id) VALUES (71, 71);
INSERT INTO ums_role_permission_relation (role_id, permission_id) VALUES (71, 72);
INSERT INTO ums_role_permission_relation (role_id, permission_id) VALUES (71, 73);
INSERT INTO ums_role_permission_relation (role_id, permission_id) VALUES (71, 74);

INSERT INTO ums_role_permission_relation (role_id, permission_id) VALUES (72, 73);
INSERT INTO ums_role_permission_relation (role_id, permission_id) VALUES (72, 74);

INSERT INTO ums_role_permission_relation (role_id, permission_id) VALUES (73, 73);

INSERT INTO ums_user_role_relation (user_id, role_id) VALUES (2, 71);

INSERT INTO ums_user_role_relation (user_id, role_id) VALUES (3, 72);
