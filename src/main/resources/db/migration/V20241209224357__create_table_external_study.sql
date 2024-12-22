create table external_study(
    id varchar(255) primary key,
    title varchar(255) not null,
    link varchar(500) not null,
    technologies varchar(255) null,
    roles varchar(255) null,
    deadline_date date,
    created_at timestamp not null,
    updated_at timestamp not null
)
