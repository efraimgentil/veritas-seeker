--liquibase formatted sql
--changeset efraimgentil:002

CREATE TABLE polling_despesas (
    id bigserial primary key,
    documento_id bigint unique,
    ano int not null,
    mes int not null
);