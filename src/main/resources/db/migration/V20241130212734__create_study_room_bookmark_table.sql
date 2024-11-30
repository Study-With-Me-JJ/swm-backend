create table if not exists study_room_bookmark(
    id bigserial primary key,
    user_id uuid references users (id) not null,
    study_room_id bigserial references study_room (id) not null,
    deleted_at timestamp null,
    created_at timestamp not null,
    updated_at timestamp not null
);

