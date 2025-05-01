alter table study_participation
add column if not exists file_url varchar(300),
add column if not exists file_name varchar(255);
