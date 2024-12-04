create type day_of_week as enum ('MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY');

create table if not exists study_room_day_off(
    id bigserial primary key,
    study_room_id bigint references study_room (id) not null,
    day_of_week day_of_week not null
);
