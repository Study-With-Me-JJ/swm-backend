package com.jj.swm.domain.studyroom.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StudyRoomOption {
    WIFI("와이파이"), ELECTRICAL("전기"), CHAIR_DESK("의자/테이블"),
    MART("편의점"), PRINTING("복사/인쇄기"), FULL_MIRROR("전신 거울"),
    FOODS("음식물 반입가능"), INTERNAL_TOILET("내부 화장실"), NO_SMOKE("금연"),
    PARKING("주차 가능"), PC_NOTEBOOK("PC/노트북"), TV_BEAM_PROJECT("TV/빔 프로젝터"),
    WATER("식수 제공"), WHITEBOARD("화이트보드"), ALCOHOL("주류 반입 가능"),
    SCREEN("스크린"), MIKE("음향/마이크");

    private final String description;

}
