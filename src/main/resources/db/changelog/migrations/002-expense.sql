--liquibase formatted sql
--changeset efraimgentil:002

CREATE TABLE expense (
    id bigint primary key,
    hash text not null unique,
    year int not null,
    month int not null
);

CREATE SEQUENCE IF NOT EXISTS expense_id_seq AS BIGINT INCREMENT BY 500 START WITH 1 CACHE 2;

CREATE TABLE expense_document (
    expense_id bigint not null,
    body json not null,
    FOREIGN KEY (expense_id) REFERENCES expense(id)
)

