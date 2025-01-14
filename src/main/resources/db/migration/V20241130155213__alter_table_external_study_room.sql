alter table external_study_room
    alter column naver_map_url type varchar(500) using naver_map_url::varchar(500),
    alter column thumbnail type varchar(500) using thumbnail::varchar(500);

