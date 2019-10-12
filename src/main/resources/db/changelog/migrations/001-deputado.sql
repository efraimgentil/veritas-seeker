--liquibase formatted sql
--changeset efraimgentil:001

CREATE TABLE polling_deputado (
    id bigserial primary key,
    deputado_id bigint unique,
    last_pull date not null
);