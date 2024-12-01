create TYPE study_room_type as enum ('MEETING', 'PARTY', 'STUDY');

create table if not exists study_room_type_info(
    id bigserial primary key,
    study_room_id bigint references study_room (id) not null,
    type study_room_type not null,
    deleted_at timestamp null,
    created_at timestamp not null,
    updated_at timestamp not null
);
