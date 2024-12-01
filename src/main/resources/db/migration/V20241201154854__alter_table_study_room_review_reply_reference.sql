alter table study_room_review_reply
drop constraint study_room_review_reply_study_room_id_fkey;

alter table study_room_review_reply
drop column if exists study_room_id;

drop sequence if exists study_room_review_reply_study_room_id_seq;

alter table study_room_review_reply
add column study_room_review_id bigint references study_room_review (id) not null;
