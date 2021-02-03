--liquibase formatted sql
--changeset efraimgentil:002

CREATE TABLE polling_expense (
    id bigserial primary key,
    hash text not null unique,
    year int not null,
    month int not null
);

CREATE TABLE expense_document (
    polling_expense_id bigint not null,
    body json not null,
    FOREIGN KEY (polling_expense_id) REFERENCES polling_expense(id)
)