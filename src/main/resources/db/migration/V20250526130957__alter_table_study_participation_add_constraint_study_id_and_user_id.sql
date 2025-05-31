CREATE UNIQUE INDEX study_participation_study_id_and_user_id_unique
    ON study_participation (study_id, user_id)
    WHERE deleted_at is null;
