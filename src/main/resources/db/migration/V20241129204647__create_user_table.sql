create type user_role as enum ('USER', 'ROOM_ADMIN', 'ADMIN');

create table if not exists users(
    id uuid primary key,
    nickname varchar(50) not null,
    profile_image_url varchar(300) null,
    deleted_at timestamp null,
    role user_role not null,
    created_at timestamp not null,
    updated_at timestamp not null
);

