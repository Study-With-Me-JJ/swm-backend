alter table study_room_bookmark ALTER COLUMN study_room_id DROP DEFAULT;
DROP SEQUENCE IF EXISTS study_room_bookmark_study_room_id_seq;

alter table study_room_like ALTER COLUMN study_room_id DROP DEFAULT;
DROP SEQUENCE IF EXISTS study_room_like_study_room_id_seq;

alter table study_room_qna ALTER COLUMN study_room_id DROP DEFAULT;
DROP SEQUENCE IF EXISTS study_room_qna_study_room_id_seq;

alter table study_room_reserve_type ALTER COLUMN study_room_id DROP DEFAULT;
DROP SEQUENCE IF EXISTS study_room_reserve_type_study_room_id_seq;

alter table study_room_review ALTER COLUMN study_room_id DROP DEFAULT;
DROP SEQUENCE IF EXISTS study_room_review_study_room_id_seq;

alter table study_room_tag ALTER COLUMN study_room_id DROP DEFAULT;
DROP SEQUENCE IF EXISTS study_room_tag_study_room_id_seq;
