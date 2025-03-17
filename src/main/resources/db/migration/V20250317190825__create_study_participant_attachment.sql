create table if not exists study_participant_attachment(
    id bigserial primary key,
    study_participant_id bigint references study_participant (id) not null,
    file_url varchar(300) not null
);
