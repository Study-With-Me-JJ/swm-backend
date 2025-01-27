package com.jj.swm.domain.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum KoreaRegion {
    JONGNO_GU("종로구"),
    JUNG_GU("중구"),
    YONGSAN_GU("용산구"),
    SEONGDONG_GU("성동구"),
    GWANGJIN_GU("광진구"),
    DONGDAEMUN_GU("동대문구"),
    JUNGNANG_GU("중랑구"),
    SEONGBUK_GU("성북구"),
    GANGBUK_GU("강북구"),
    DOBONG_GU("도봉구"),
    NOWON_GU("노원구"),
    EUNPYEONG_GU("은평구"),
    SEODAEMUN_GU("서대문구"),
    MAPO_GU("마포구"),
    YANGCHEON_GU("양천구"),
    GANGSEO_GU("강서구"),
    GURO_GU("구로구"),
    GEUMCHEON_GU("금천구"),
    YEONGDEUNGPO_GU("영등포구"),
    DONGJAK_GU("동작구"),
    GWANAK_GU("관악구"),
    SEOCHO_GU("서초구"),
    GANGNAM_GU("강남구"),
    SONGPA_GU("송파구"),
    GANGDONG_GU("강동구");

    private final String korName; // 한국어 이름
}
