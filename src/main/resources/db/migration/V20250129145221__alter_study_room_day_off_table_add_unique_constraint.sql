alter table study_room_day_off
add constraint unique_day_of_week_per_study_room
unique (study_room_id, day_of_week);
