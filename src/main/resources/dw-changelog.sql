-- liquibase formatted sql

-- changeset liquibase:1

CREATE TABLE auth_user (
    auth_user_id SERIAL,
    email VARCHAR(256) not null unique,
    password VARCHAR(256) not null,
    salt VARCHAR(256) not null,
    first_name VARCHAR(256) not null,
    last_name VARCHAR(256) not null,
    PRIMARY KEY (auth_user_id)
);

CREATE TABLE doc (
    doc_id SERIAL,
    name VARCHAR(256) not null,
    mime_type VARCHAR(256) not null,
    created_by_user_id int not null,
    updated_by_user_id int not null,
    created_datetime TIMESTAMPTZ not null,
    updated_datetime TIMESTAMPTZ not null,
    PRIMARY KEY (doc_id),
    FOREIGN KEY (created_by_user_id) REFERENCES auth_user (auth_user_id) ON UPDATE CASCADE,
    FOREIGN KEY (updated_by_user_id) REFERENCES auth_user (auth_user_id) ON UPDATE CASCADE
);

CREATE TABLE doc_version (
    doc_version_id SERIAL,
    version_num int not null,
    doc_id int not null,
    data bytea not null,
    created_datetime TIMESTAMPTZ not null,
    created_by_user_id int not null,
    PRIMARY KEY (doc_version_id),
    FOREIGN KEY (doc_id) REFERENCES doc (doc_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (created_by_user_id) REFERENCES auth_user (auth_user_id) ON UPDATE CASCADE
);

CREATE TABLE doc_task (
    doc_task_id SERIAL,
    doc_id int not null,
    name VARCHAR(256) not null,
    status VARCHAR(256) not null,
    content TEXT,
    created_by_user_id int not null,
    updated_by_user_id int not null,
    created_datetime TIMESTAMPTZ not null,
    updated_datetime TIMESTAMPTZ not null,
    PRIMARY KEY (doc_task_id),
    FOREIGN KEY (doc_id) REFERENCES doc (doc_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (created_by_user_id) REFERENCES auth_user (auth_user_id) ON UPDATE CASCADE
);

-- changeset liquibase:2

CREATE TABLE batch_run (
    batch_run_id SERIAL,
    created_datetime TIMESTAMPTZ not null,
    success boolean not null,
    error_text TEXT,
    PRIMARY KEY (batch_run_id)
);

-- changeset liquibase:3

ALTER TABLE batch_run
  ADD runtime_ms int;

