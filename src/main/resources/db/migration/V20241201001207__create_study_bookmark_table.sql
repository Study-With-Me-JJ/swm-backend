create table if not exists study_bookmark (
    id bigserial primary key,
    study_id bigserial references study (id) not null,
    user_id uuid references users (id) not null
)
