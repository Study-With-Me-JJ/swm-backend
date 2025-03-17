create table if not exists study_participant_link(
    id bigserial primary key,
    study_participant_id bigint references study_participant (id) not null,
    link varchar(300) not null
);
