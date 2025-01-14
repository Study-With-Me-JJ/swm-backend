alter table study_room_qna alter column parent_id drop not null;

alter table study_room_qna ALTER COLUMN parent_id DROP DEFAULT;
DROP SEQUENCE IF EXISTS study_room_qna_parent_id_seq;
