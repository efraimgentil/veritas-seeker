--liquibase formatted sql
--changeset efraimgentil:001

CREATE TABLE polling_congressman (
    id bigserial primary key,
    congressman_id bigint unique,
    last_pull date not null
);