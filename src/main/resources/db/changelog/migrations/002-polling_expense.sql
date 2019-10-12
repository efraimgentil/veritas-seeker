--liquibase formatted sql
--changeset efraimgentil:002

CREATE TABLE polling_expense (
    id bigserial primary key,
    document_id bigint unique,
    year int not null,
    month int not null
);

CREATE TABLE expense_document (
    polling_expense_id bigint not null,
    hash not null,
    body json not null,
    FOREIGN KEY (polling_expense_id) REFERENCES polling_expense(id)
)