create type study_status as enum ('ACTIVE', 'INACTIVE');
create type study_category as enum ('ALGORITHM', 'DEVELOPMENT');

create table if not exists study (
    id bigserial primary key,
    user_id uuid references users (id) not null,
    title varchar(100) not null,
    content text not null,
    deleted_at timestamp,
    category study_category not null,
    like_count integer not null,
    comment_count integer not null,
    status study_status not null,
    view_count integer not null,
    thumbnail varchar(300),
    created_at timestamp not null,
    updated_at timestamp not null
)
