alter table study_tag ALTER COLUMN study_id DROP DEFAULT;
DROP SEQUENCE IF EXISTS study_tag_study_id_seq;

alter table study_image ALTER COLUMN study_id DROP DEFAULT;
DROP SEQUENCE IF EXISTS study_image_study_id_seq;

alter table study_recruitment_position ALTER COLUMN study_id DROP DEFAULT;
DROP SEQUENCE IF EXISTS study_recruitment_position_study_id_seq;

alter table study_participant ALTER COLUMN study_recruitment_position_id DROP DEFAULT;
DROP SEQUENCE IF EXISTS study_participant_study_recruitment_position_id_seq;

alter table study_bookmark ALTER COLUMN study_id DROP DEFAULT;
DROP SEQUENCE IF EXISTS study_bookmark_study_id_seq;

alter table study_comment ALTER COLUMN study_id DROP DEFAULT;
DROP SEQUENCE IF EXISTS study_comment_study_id_seq;

alter table study_like ALTER COLUMN study_id DROP DEFAULT;
DROP SEQUENCE IF EXISTS study_like_study_id_seq;
