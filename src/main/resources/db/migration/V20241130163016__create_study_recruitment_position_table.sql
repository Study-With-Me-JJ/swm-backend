create table if not exists study_recruitment_position (
    id bigserial primary key,
    study_id bigserial references study (id) not null,
    title varchar(50) not null,
    headcount integer not null
)
