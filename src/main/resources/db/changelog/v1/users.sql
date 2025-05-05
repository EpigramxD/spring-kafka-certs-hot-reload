--liquibase formatted sql

--changeset epigram:create_users_table

create table if not exists users (
id bigserial,
username varchar(45) not null,
password varchar(200) not null,
enabled int not null,
primary key (id)
);


--rollback drop table users;