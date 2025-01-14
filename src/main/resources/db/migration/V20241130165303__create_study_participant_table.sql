create type study_participant_status as enum ('PENDING', 'REJECTED', 'ACCEPTED');

create table if not exists study_participant (
    id bigserial primary key,
    study_recruitment_position_id bigserial references study_recruitment_position (id) not null,
    user_id uuid references users (id) not null,
    status study_participant_status not null
)
