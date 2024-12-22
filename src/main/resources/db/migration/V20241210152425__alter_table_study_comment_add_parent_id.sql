ALTER TABLE study_comment
ADD COLUMN parent_id bigint REFERENCES study_comment (id);
