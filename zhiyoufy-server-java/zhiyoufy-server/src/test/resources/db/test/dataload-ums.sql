-- password: "Bry3jHhrhl!"
INSERT INTO ums_user (id, username, password, email, sys_admin, admin) VALUES (
	1, 'sysadmin', '$2a$10$o5q0N3f8n93F40ZRG4mwCuscW68diGvx6Q7lIUTUcFELgqhY9u08O',
	'sysadmin@example.com', TRUE, TRUE);
INSERT INTO ums_user (id, username, password, email, sys_admin, admin) VALUES (
	2, 'admin', '$2a$10$o5q0N3f8n93F40ZRG4mwCuscW68diGvx6Q7lIUTUcFELgqhY9u08O',
	'admin@example.com', FALSE, TRUE);
INSERT INTO ums_user (id, username, password, email) VALUES (
	3, 'set001', '$2a$10$o5q0N3f8n93F40ZRG4mwCuscW68diGvx6Q7lIUTUcFELgqhY9u08O',
	'set001@example.com');
INSERT INTO ums_user (id, username, password, email) VALUES (
	4, 'swe001', '$2a$10$o5q0N3f8n93F40ZRG4mwCuscW68diGvx6Q7lIUTUcFELgqhY9u08O',
	'swe001@example.com');
INSERT INTO ums_user (id, username, password, email) VALUES (
	5, 'devops001', '$2a$10$o5q0N3f8n93F40ZRG4mwCuscW68diGvx6Q7lIUTUcFELgqhY9u08O',
	'devops001@example.com');

