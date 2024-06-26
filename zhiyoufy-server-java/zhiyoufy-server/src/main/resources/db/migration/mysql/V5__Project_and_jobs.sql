CREATE TABLE pms_project (
    id BIGINT NOT NULL AUTO_INCREMENT primary key,

    name VARCHAR(64) NOT NULL,

    description VARCHAR(1000) NOT NULL DEFAULT '',

    created_time datetime(3) DEFAULT CURRENT_TIMESTAMP(3),
    created_by VARCHAR(32) NOT NULL,

    modified_time datetime(3) DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    modified_by VARCHAR(32) NOT NULL,

    UNIQUE (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE pms_project_user_relation (
    id BIGINT NOT NULL AUTO_INCREMENT primary key,

    project_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,

    is_owner BOOLEAN DEFAULT FALSE,
    is_editor BOOLEAN DEFAULT FALSE,

    constraint uc_project_user_relation unique (project_id, user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE pms_job_folder (
    id BIGINT NOT NULL AUTO_INCREMENT primary key,

    project_id BIGINT NOT NULL,
    parent_id BIGINT NOT NULL DEFAULT 0,

    name VARCHAR(128) NOT NULL,
    description VARCHAR(1000) NOT NULL DEFAULT '',

    created_time datetime(3) DEFAULT CURRENT_TIMESTAMP(3),
	created_by VARCHAR(32) NOT NULL,

	modified_time datetime(3) DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
	modified_by VARCHAR(32) NOT NULL,

    constraint uc_job_folder_project_id_name unique (project_id, name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE pms_job_template (
    id BIGINT NOT NULL AUTO_INCREMENT primary key,

    project_id BIGINT NOT NULL,
    folder_id BIGINT NOT NULL,

    name VARCHAR(128) NOT NULL,
    description VARCHAR(512) NOT NULL DEFAULT '',

    job_path VARCHAR(512) NOT NULL,
    worker_labels VARCHAR(512),
    timeout_seconds INT NOT NULL,

    base_conf_path VARCHAR(512),
    private_conf_path VARCHAR(512),
    config_singles VARCHAR(2048),
    config_collections VARCHAR(2048),

    extra_args VARCHAR(512) NOT NULL DEFAULT '',

    created_time datetime(3) DEFAULT CURRENT_TIMESTAMP(3),
	created_by VARCHAR(32) NOT NULL,

	modified_time datetime(3) DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
	modified_by VARCHAR(32) NOT NULL,

    constraint uc_job_template_project_id_name unique (project_id, name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
