DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_type WHERE typname = 'study_participant_status') THEN
        ALTER TYPE study_participant_status RENAME TO study_participation_status;
    END IF;
END $$;
