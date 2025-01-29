alter table study_room_type_info
add constraint unique_type_per_study_room
unique (study_room_id, type);
