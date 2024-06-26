CREATE TABLE ums_user (
    id BIGINT NOT NULL AUTO_INCREMENT primary key,

    username VARCHAR(32) NOT NULL,
    password VARCHAR(255) NOT NULL,
    email varchar(100) NOT NULL,
    `note` varchar(500) DEFAULT NULL COMMENT '备注信息',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `login_time` datetime DEFAULT NULL COMMENT '最后登录时间',

    sys_admin BOOLEAN NOT NULL DEFAULT FALSE,
    admin BOOLEAN NOT NULL DEFAULT FALSE,
    enabled BOOLEAN NOT NULL DEFAULT TRUE COMMENT '帐号启用状态：false->禁用；true->启用',

    constraint uc_ums_user_username unique (username),
    constraint uc_ums_user_email unique (email)
);

CREATE TABLE ums_role (
    id BIGINT NOT NULL AUTO_INCREMENT primary key,

    name VARCHAR(128) NOT NULL,
    `description` varchar(500) DEFAULT NULL COMMENT '描述',
    `user_count` int DEFAULT NULL COMMENT '用户数量',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',

    builtin BOOLEAN NOT NULL default true,
    enabled BOOLEAN NOT NULL default true,

    constraint uc_ums_role unique (name)
);

CREATE TABLE ums_permission (
    id BIGINT NOT NULL AUTO_INCREMENT primary key,

    name VARCHAR(128) NOT NULL,
    `description` varchar(500) DEFAULT NULL COMMENT '描述',

    builtin BOOLEAN NOT NULL default true,

    constraint uc_ums_permission unique (name)
);

CREATE TABLE ums_user_role_relation (
    id BIGINT NOT NULL AUTO_INCREMENT primary key,

    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,

    constraint uc_ums_user_role_relation unique (user_id, role_id)
);

CREATE TABLE ums_role_permission_relation (
    id BIGINT NOT NULL AUTO_INCREMENT primary key,

    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,

    constraint uc_ums_role_permission_relation unique (role_id, permission_id)
);

CREATE TABLE `ums_user_login_log` (
  id BIGINT NOT NULL AUTO_INCREMENT primary key,

  user_id BIGINT NOT NULL,
  `create_time` datetime NOT NULL,

  `ip` varchar(64) NOT NULL
);

CREATE TABLE ums_token (
    id BIGINT NOT NULL AUTO_INCREMENT primary key,

    user_id BIGINT NOT NULL,

    `value` varchar(64) NOT NULL,
    `create_time` datetime NOT NULL,
    `expire_time` datetime NOT NULL,

    constraint uc_ums_token unique (`value`)
);
