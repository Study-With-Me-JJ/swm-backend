create table if not exists study_room_image(
    id bigserial primary key,
    study_room_id bigint references study_room (id) not null,
    image_url varchar(300) not null
);
