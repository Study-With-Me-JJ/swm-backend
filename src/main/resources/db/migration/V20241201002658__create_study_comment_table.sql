create table if not exists study_comment (
    id bigserial primary key,
    study_id bigserial references study (id) not null,
    user_id uuid references users (id) not null,
    content varchar(255) not null,
    deleted_at timestamp,
    created_at timestamp not null,
    updated_at timestamp not null
)
