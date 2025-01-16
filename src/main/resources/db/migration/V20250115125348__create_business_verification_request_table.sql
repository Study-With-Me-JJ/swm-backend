CREATE TYPE inspection_status AS ENUM (
    'PENDING',
    'APPROVED',
    'REJECTED'
);

create table if not exists business_verification_request(
    id bigserial primary key,
    user_id uuid references users (id) not null,
    user_name varchar(30) not null,
    user_role user_role not null,
    user_nickname varchar(50) not null,
    business_number varchar(10) not null,
    business_owner_name varchar(30) not null,
    business_registration_date varchar(10) not null,
    business_name varchar(50) not null,
    inspection_status inspection_status not null,
    created_at timestamp not null,
    updated_at timestamp not null,
    deleted_at timestamp
);
