alter table study_room_option_info
add constraint unique_option_per_study_room
unique (study_room_id, option);
