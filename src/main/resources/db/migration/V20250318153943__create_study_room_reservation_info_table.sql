create type approval_status as enum ('PENDING', 'APPROVED', 'REJECTED');

create table if not exists study_room_reservation_info (
       id bigserial primary key,
       study_room_id bigint references study_room (id) not null,
       study_room_reserve_type_id bigint references study_room_reserve_type (id) not null,
       user_id uuid references users (id) not null,
       reserver_phone_number varchar(20) not null,
       reserver_name varchar(20) not null,
       check_in_time timestamp not null,
       check_out_time timestamp not null,
       headcount integer not null,
       memo varchar(200),
       usage_time integer not null,
       approval_status approval_status not null,
       created_at timestamp not null,
       updated_at timestamp not null,
       deleted_at timestamp
);
