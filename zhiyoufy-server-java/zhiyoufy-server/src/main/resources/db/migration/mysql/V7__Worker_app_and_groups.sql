CREATE TABLE wms_worker_app (
    id BIGINT NOT NULL AUTO_INCREMENT primary key,

    name VARCHAR(64) NOT NULL,
    worker_labels VARCHAR(512) DEFAULT NULL,

    description VARCHAR(1000) NOT NULL DEFAULT '',

    need_config_be_json BOOLEAN DEFAULT FALSE,

    created_time datetime(3) DEFAULT CURRENT_TIMESTAMP(3),
    created_by VARCHAR(32) NOT NULL,

    modified_time datetime(3) DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    modified_by VARCHAR(32) NOT NULL,

    UNIQUE (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE wms_worker_app_user_relation (
    id BIGINT NOT NULL AUTO_INCREMENT primary key,

    worker_app_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,

    is_owner BOOLEAN DEFAULT FALSE,
    is_editor BOOLEAN DEFAULT FALSE,

    constraint uc_worker_app_user_relation unique (worker_app_id, user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE wms_worker_group (
    id BIGINT NOT NULL AUTO_INCREMENT primary key,

    worker_app_id BIGINT NOT NULL,

    name VARCHAR(128) NOT NULL,
    worker_labels VARCHAR(512) DEFAULT NULL,

    description VARCHAR(1000) NOT NULL DEFAULT '',

    created_time datetime(3) DEFAULT CURRENT_TIMESTAMP(3),
	created_by VARCHAR(32) NOT NULL,

	modified_time datetime(3) DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
	modified_by VARCHAR(32) NOT NULL,

    constraint uc_worker_group_worker_app_id_name unique (worker_app_id, name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE wms_group_token (
    id BIGINT NOT NULL AUTO_INCREMENT primary key,

    worker_app_id BIGINT NOT NULL,
    worker_group_id BIGINT NOT NULL,

    name VARCHAR(128) NOT NULL,
	secret VARCHAR(128) NOT NULL,
	expiry_time DATETIME,

	description VARCHAR(512) NOT NULL DEFAULT '',

    created_time datetime(3) DEFAULT CURRENT_TIMESTAMP(3),
	created_by VARCHAR(32) NOT NULL,

	modified_time datetime(3) DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
	modified_by VARCHAR(32) NOT NULL,

    constraint uc_group_token_group_id_name unique (worker_group_id, name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
