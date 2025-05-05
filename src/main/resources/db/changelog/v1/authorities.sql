--liquibase formatted sql

--changeset epigram:create_authorities_table

create table if not exists authorities (
id bigserial,
username varchar(45) not null,
authority varchar(45) not null,
primary key (id)
);

--rollback drop table authorities;

