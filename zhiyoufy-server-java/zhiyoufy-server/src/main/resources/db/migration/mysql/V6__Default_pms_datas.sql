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