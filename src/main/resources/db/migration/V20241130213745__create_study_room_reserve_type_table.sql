create table if not exists study_room_reserve_type(
    id bigserial primary key,
    study_room_id bigserial references study_room (id) not null,
    max_headcount integer not null,
    reservation_option varchar(50) not null,
    price_per_hour integer not null,
    deleted_at timestamp null,
    created_at timestamp not null,
    updated_at timestamp not null
);

