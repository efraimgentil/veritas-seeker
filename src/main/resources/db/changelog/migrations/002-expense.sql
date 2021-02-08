--liquibase formatted sql
--changeset efraimgentil:002

CREATE TABLE expense (
    id bigserial primary key,
    hash text not null unique,
    year int not null,
    month int not null
);

CREATE TABLE expense_document (
    expense_id bigint not null,
    body json not null,
    FOREIGN KEY (expense_id) REFERENCES expense(id)
)