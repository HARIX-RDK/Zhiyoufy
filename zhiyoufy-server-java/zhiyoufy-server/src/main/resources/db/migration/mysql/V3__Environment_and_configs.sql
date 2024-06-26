CREATE TABLE ems_environment (
    id BIGINT NOT NULL AUTO_INCREMENT primary key,

    parent_id BIGINT,

    name VARCHAR(64) NOT NULL,

    description VARCHAR(512) DEFAULT NULL,

    worker_labels VARCHAR(512) DEFAULT NULL,
    extra_args VARCHAR(1024) DEFAULT NULL,

    created_time datetime(3) DEFAULT CURRENT_TIMESTAMP(3),
    created_by VARCHAR(32) NOT NULL,

    modified_time datetime(3) DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    modified_by VARCHAR(32) NOT NULL,

    UNIQUE (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE ems_environment_user_relation (
    id BIGINT NOT NULL AUTO_INCREMENT primary key,

    environment_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    is_owner BOOLEAN DEFAULT FALSE,
    is_editor BOOLEAN DEFAULT FALSE,

    constraint uc_environment_user_relation unique (environment_id, user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE ems_environment_list (
    id BIGINT NOT NULL AUTO_INCREMENT primary key,

    name VARCHAR(64) NOT NULL,

    description VARCHAR(512) DEFAULT NULL,

    created_time datetime(3) DEFAULT CURRENT_TIMESTAMP(3),
    created_by VARCHAR(32) NOT NULL,

    modified_time datetime(3) DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    modified_by VARCHAR(32) NOT NULL,

    UNIQUE (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE ems_environment_list_environment_relation (
    id BIGINT NOT NULL AUTO_INCREMENT primary key,

    environment_list_id BIGINT NOT NULL,
    environment_id BIGINT NOT NULL,

    constraint uc_environment_list_environment_relation unique (environment_list_id, environment_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE ems_environment_list_user_relation (
    id BIGINT NOT NULL AUTO_INCREMENT primary key,

    environment_list_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    is_owner BOOLEAN DEFAULT FALSE,
    is_editor BOOLEAN DEFAULT FALSE,

    constraint uc_environment_list_user_relation unique (environment_list_id, user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE ems_config_single (
    id BIGINT NOT NULL AUTO_INCREMENT primary key,

    environment_id BIGINT NOT NULL,

    name VARCHAR(128) NOT NULL,

    config_value TEXT NOT NULL,

    created_time datetime(3) DEFAULT CURRENT_TIMESTAMP(3),
    created_by VARCHAR(32) NOT NULL,

    modified_time datetime(3) DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    modified_by VARCHAR(32) NOT NULL,

    constraint uc_config_single_environment_id_name unique (environment_id, name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE ems_config_collection (
    id BIGINT NOT NULL AUTO_INCREMENT primary key,

    environment_id BIGINT NOT NULL,

    name VARCHAR(128) NOT NULL,

    created_time datetime(3) DEFAULT CURRENT_TIMESTAMP(3),
	created_by VARCHAR(32) NOT NULL,

	modified_time datetime(3) DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
	modified_by VARCHAR(32) NOT NULL,

    constraint uc_config_collection_environment_id_name unique (environment_id, name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE ems_config_item (
    id BIGINT NOT NULL AUTO_INCREMENT primary key,

    environment_id BIGINT NOT NULL,
    collection_id BIGINT NOT NULL,

    name VARCHAR(128) NOT NULL,

    config_value TEXT NOT NULL,

    tags VARCHAR(1024) NOT NULL DEFAULT '',

    sort INT NOT NULL DEFAULT 0,
    disabled BOOLEAN NOT NULL DEFAULT FALSE,

    in_use BOOLEAN NOT NULL DEFAULT FALSE,
    usage_id VARCHAR(128),
    usage_timeout_at  DATETIME(3),

    constraint uc_config_item_collection_id_name unique (collection_id, name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
