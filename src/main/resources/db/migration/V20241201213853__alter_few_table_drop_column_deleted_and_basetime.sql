alter table study_room_bookmark
drop column if exists deleted_at,
drop column if exists created_at,
drop column if exists updated_at;

alter table study_room_like
drop column if exists deleted_at,
drop column if exists created_at,
drop column if exists updated_at;

alter table study_room_option_info
drop column if exists deleted_at,
drop column if exists created_at,
drop column if exists updated_at;

alter table study_room_type_info
drop column if exists deleted_at,
drop column if exists created_at,
drop column if exists updated_at;
