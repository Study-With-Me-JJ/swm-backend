alter table study_room
add constraint study_room_user_id_fk
foreign key (user_id) references users (id);
