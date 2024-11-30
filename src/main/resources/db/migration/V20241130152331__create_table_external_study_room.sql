CREATE TYPE korea_region AS ENUM (
    'JONGNO_GU',
    'JUNG_GU',
    'YONGSAN_GU',
    'SEONGDONG_GU',
    'GWANGJIN_GU',
    'DONGDAEMUN_GU',
    'JUNGNANG_GU',
    'SEONGBUK_GU',
    'GANGBUK_GU',
    'DOBONG_GU',
    'NOWON_GU',
    'EUNPYEONG_GU',
    'SEODAEMUN_GU',
    'MAPO_GU',
    'YANGCHEON_GU',
    'GANGSEO_GU',
    'GURO_GU',
    'GEUMCHEON_GU',
    'YEONGDEUNGPO_GU',
    'DONGJAK_GU',
    'GWANAK_GU',
    'SEOCHO_GU',
    'GANGNAM_GU',
    'SONGPA_GU',
    'GANGDONG_GU'
    );

create table external_study_room(
    id bigint primary key,
    name varchar(50) not null,
    address varchar(200) not null,
    number varchar(30),
    url varchar(300),
    korea_region korea_region not null,
    deleted_at timestamp,
    naver_map_url varchar(300) not null,
    thumbnail varchar(300),
    description varchar(500),
    created_at timestamp not null,
    updated_at timestamp not null
);