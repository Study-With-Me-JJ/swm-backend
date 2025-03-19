alter table study_recruitment_position
add constraint check_headcount CHECK ( headcount <= 100 )
