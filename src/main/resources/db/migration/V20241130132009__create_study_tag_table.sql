create table if not exists study_tag (
    id bigserial primary key,
    study_id bigserial references study (id) not null,
    name varchar(50) not null
)
