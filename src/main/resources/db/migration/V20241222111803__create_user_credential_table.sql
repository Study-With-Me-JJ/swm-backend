create type provider as enum ('GOOGLE', 'GITHUB', 'NAVER', 'KAKAO');

create table if not exists user_credential(
    id bigint primary key,
    user_id uuid references users (id) not null,
    provider provider,
    value varchar(200) not null,
    login_id varchar(255) unique
);
