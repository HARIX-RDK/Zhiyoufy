INSERT INTO ems_environment (id, parent_id, name, worker_labels,created_by, modified_by) VALUES (
	10, NULL, 'env_for_configs', '{"test_env_configs":true}','spring-test', 'spring-test');

-- user: set001
INSERT INTO ems_environment_user_relation (environment_id, user_id, is_owner, is_editor)
	VALUES (10, 3, TRUE, TRUE);

INSERT INTO ems_config_single (environment_id, name, config_value, created_by, modified_by)
	VALUES (10, 'ConfigSingle_001', '# ConfigSingle_001', 'spring-test', 'spring-test');
INSERT INTO ems_config_single (environment_id, name, config_value, created_by, modified_by)
	VALUES (10, 'ConfigSingle_002', '# ConfigSingle_002', 'spring-test', 'spring-test');
INSERT INTO ems_config_single (environment_id, name, config_value, created_by, modified_by)
	VALUES (10, 'ConfigSingle_003', '# ConfigSingle_003', 'spring-test', 'spring-test');
INSERT INTO ems_config_single (environment_id, name, config_value, created_by, modified_by)
	VALUES (10, 'ConfigSingle_004', '# ConfigSingle_004', 'spring-test', 'spring-test');

INSERT INTO ems_config_collection (id, environment_id, name, created_by, modified_by)
	VALUES (100, 10, 'ConfigCollection_001', 'spring-test', 'spring-test');

INSERT INTO ems_config_item (environment_id, collection_id, name, config_value, in_use, usage_id)
	VALUES (10, 100, 'ConfigItem_001', '# config_value', TRUE, 'runId-1');
INSERT INTO ems_config_item (environment_id, collection_id, name, config_value, in_use, usage_id)
	VALUES (10, 100, 'ConfigItem_002', '# config_value', TRUE, 'runId-1');
INSERT INTO ems_config_item (environment_id, collection_id, name, config_value, in_use, usage_id)
	VALUES (10, 100, 'ConfigItem_003', '# config_value', TRUE, 'runId-2');
INSERT INTO ems_config_item (environment_id, collection_id, name, config_value, in_use, usage_id)
	VALUES (10, 100, 'ConfigItem_004', '# config_value', TRUE, 'runId-2');
INSERT INTO ems_config_item (environment_id, collection_id, name, config_value, in_use, usage_id)
	VALUES (10, 100, 'ConfigItem_005', '# config_value', FALSE, 'runId-3');

INSERT INTO ems_config_collection (id, environment_id, name, created_by, modified_by)
	VALUES (110, 10, 'ConfigCollection_010', 'spring-test', 'spring-test');

INSERT INTO ems_config_item (environment_id, collection_id, name, config_value, in_use, usage_id)
	VALUES (10, 110, 'ConfigItem_001', '# ConfigCollection_010 ConfigItem_001', TRUE, 'runId-1');
INSERT INTO ems_config_item (environment_id, collection_id, name, config_value, in_use, usage_id)
	VALUES (10, 110, 'ConfigItem_002', '# ConfigCollection_010 ConfigItem_002', TRUE, 'runId-1');
INSERT INTO ems_config_item (environment_id, collection_id, name, config_value, in_use, usage_id)
	VALUES (10, 110, 'ConfigItem_003', '# ConfigCollection_010 ConfigItem_003', TRUE, 'runId-2');
INSERT INTO ems_config_item (environment_id, collection_id, name, config_value, in_use, usage_id)
	VALUES (10, 110, 'ConfigItem_004', '# ConfigCollection_010 ConfigItem_004', TRUE, 'runId-2');
INSERT INTO ems_config_item (environment_id, collection_id, name, config_value, in_use, usage_id)
	VALUES (10, 110, 'ConfigItem_005', '# ConfigCollection_010 ConfigItem_005', FALSE, '');
INSERT INTO ems_config_item (environment_id, collection_id, name, config_value, in_use, usage_id)
	VALUES (10, 110, 'ConfigItem_006', '# ConfigCollection_010 ConfigItem_006', FALSE, '');
INSERT INTO ems_config_item (environment_id, collection_id, name, config_value, in_use, usage_id)
	VALUES (10, 110, 'ConfigItem_007', '# ConfigCollection_010 ConfigItem_007', FALSE, '');
INSERT INTO ems_config_item (environment_id, collection_id, name, config_value, in_use, usage_id)
	VALUES (10, 110, 'ConfigItem_008', '# ConfigCollection_010 ConfigItem_008', FALSE, '');
INSERT INTO ems_config_item (environment_id, collection_id, name, config_value, in_use, usage_id)
	VALUES (10, 110, 'ConfigItem_009', '# ConfigCollection_010 ConfigItem_009', FALSE, '');
INSERT INTO ems_config_item (environment_id, collection_id, name, config_value, in_use, usage_id)
	VALUES (10, 110, 'ConfigItem_010', '# ConfigCollection_010 ConfigItem_010', FALSE, '');

INSERT INTO ems_config_collection (id, environment_id, name, created_by, modified_by)
	VALUES (111, 10, 'ConfigCollection_011', 'spring-test', 'spring-test');

INSERT INTO ems_config_item (environment_id, collection_id, name, config_value, in_use, usage_id)
	VALUES (10, 111, 'ConfigItem_001', '# ConfigCollection_011 ConfigItem_001', TRUE, 'runId-1');
INSERT INTO ems_config_item (environment_id, collection_id, name, config_value, in_use, usage_id)
	VALUES (10, 111, 'ConfigItem_002', '# ConfigCollection_011 ConfigItem_002', TRUE, 'runId-1');
INSERT INTO ems_config_item (environment_id, collection_id, name, config_value, in_use, usage_id)
	VALUES (10, 111, 'ConfigItem_003', '# ConfigCollection_011 ConfigItem_003', TRUE, 'runId-2');
INSERT INTO ems_config_item (environment_id, collection_id, name, config_value, in_use, usage_id)
	VALUES (10, 111, 'ConfigItem_004', '# ConfigCollection_011 ConfigItem_004', TRUE, 'runId-2');
INSERT INTO ems_config_item (environment_id, collection_id, name, config_value, in_use, usage_id)
	VALUES (10, 111, 'ConfigItem_005', '# ConfigCollection_011 ConfigItem_005', FALSE, '');
INSERT INTO ems_config_item (environment_id, collection_id, name, config_value, in_use, usage_id)
	VALUES (10, 111, 'ConfigItem_006', '# ConfigCollection_011 ConfigItem_006', FALSE, '');
INSERT INTO ems_config_item (environment_id, collection_id, name, config_value, in_use, usage_id)
	VALUES (10, 111, 'ConfigItem_007', '# ConfigCollection_011 ConfigItem_007', FALSE, '');
INSERT INTO ems_config_item (environment_id, collection_id, name, config_value, in_use, usage_id)
	VALUES (10, 111, 'ConfigItem_008', '# ConfigCollection_011 ConfigItem_008', FALSE, '');
INSERT INTO ems_config_item (environment_id, collection_id, name, config_value, in_use, usage_id)
	VALUES (10, 111, 'ConfigItem_009', '# ConfigCollection_011 ConfigItem_009', FALSE, '');
INSERT INTO ems_config_item (environment_id, collection_id, name, config_value, in_use, usage_id)
	VALUES (10, 111, 'ConfigItem_010', '# ConfigCollection_011 ConfigItem_010', FALSE, '');