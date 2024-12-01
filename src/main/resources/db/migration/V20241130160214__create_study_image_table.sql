create table if not exists study_image (
    id bigserial primary key,
    study_id bigserial references study (id) not null,
    image_url varchar(300) not null
)
