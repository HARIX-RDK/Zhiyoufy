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