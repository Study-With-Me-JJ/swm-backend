create table if not exists study_room_review_image(
    id bigserial primary key,
    study_room_review_id bigint references study_room_review (id) not null,
    image_url varchar(300) not null
);
