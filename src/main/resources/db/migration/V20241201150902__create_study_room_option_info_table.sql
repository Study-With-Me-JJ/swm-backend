create TYPE study_room_option as enum (
    'WIFI', 'ELECTRICAL', 'CHAIR_DESK',
    'MART', 'PRINTING', 'FULL_MIRROR',
    'FOODS', 'INTERNAL_TOILET', 'NO_SMOKE',
    'PARKING', 'PC_NOTEBOOK', 'TV_BEAM_PROJECT',
    'WATER', 'WHITEBOARD', 'ALCOHOL',
    'SCREEN', 'MIKE'
);

create table if not exists study_room_option_info(
    id bigserial primary key,
    study_room_id bigint references study_room (id) not null,
    option study_room_option not null,
    deleted_at timestamp null,
    created_at timestamp not null,
    updated_at timestamp not null
)
