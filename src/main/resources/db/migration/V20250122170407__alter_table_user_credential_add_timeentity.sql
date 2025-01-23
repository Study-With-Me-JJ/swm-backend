alter table user_credential add column created_at timestamp not null default now();
alter table user_credential add column updated_at timestamp not null default now();
alter table user_credential add column deleted_at timestamp;
