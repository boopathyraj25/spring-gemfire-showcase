-- liquibase formatted sql
-- changeset liquibaseuser:1

-- Autogenerated: do not edit this file

CREATE TABLE account (
    id varchar(100) NOT NULL,
    name varchar(255) NOT NULL,
    PRIMARY KEY (ID)
);

-- rollback drop table account;