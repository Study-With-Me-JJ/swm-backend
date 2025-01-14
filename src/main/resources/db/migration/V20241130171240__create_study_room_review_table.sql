create table if not exists study_room_review(
    id bigserial primary key,
    user_id uuid references users (id) not null,
    study_room_id bigserial references study_room (id) not null,
    comment text not null,
    rating integer not null check ( rating >= 0 and rating <= 5 ),
    deleted_at timestamp null,
    created_at timestamp not null,
    updated_at timestamp not null
);

