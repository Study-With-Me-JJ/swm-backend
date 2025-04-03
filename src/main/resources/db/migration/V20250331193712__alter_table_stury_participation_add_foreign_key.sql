alter table study_participation add column study_id bigint not null;

alter table study_participation
add constraint study_participation_study_id_fkey foreign key (study_id) references study(id);
