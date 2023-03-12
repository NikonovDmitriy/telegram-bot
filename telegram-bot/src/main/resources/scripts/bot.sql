-- liquibase formatted sql
-- changeset anna:1
create table tasks
(
    id          serial primary key,
    chat_id     bigint,
    date_time   timestamptz,
    description text
)