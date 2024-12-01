create table if not exists study_room_tag(
    id bigserial primary key,
    study_room_id bigserial references study_room (id) not null,
    tag varchar(50) not null,
    deleted_at timestamp null,
    created_at timestamp not null,
    updated_at timestamp not null
);

