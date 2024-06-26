CREATE TABLE pms_favorite_folder (
    id BIGINT NOT NULL AUTO_INCREMENT primary key,

    project_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,

    name VARCHAR(128) NOT NULL,

	modified_time datetime(3) DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),

    constraint uc_favorite_folder_pun unique (project_id, user_id, name),

    FOREIGN KEY (project_id)
    	REFERENCES pms_project(id)
    	ON DELETE CASCADE,

    FOREIGN KEY (user_id)
        REFERENCES ums_user(id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE pms_favorite_folder_template_relation (
    id BIGINT NOT NULL AUTO_INCREMENT primary key,

	user_id BIGINT NOT NULL,
    folder_id BIGINT NOT NULL,
    template_id BIGINT NOT NULL,

    constraint uc_favorite_folder_template_relation unique (folder_id, template_id),

    FOREIGN KEY (folder_id)
		REFERENCES pms_favorite_folder(id)
		ON DELETE CASCADE,

	FOREIGN KEY (template_id)
		REFERENCES pms_job_template(id)
		ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
