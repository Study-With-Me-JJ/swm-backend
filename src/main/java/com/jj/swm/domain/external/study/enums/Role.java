package com.jj.swm.domain.external.study.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
@Schema(enumAsRef = true)
public enum Role {
    ALL("전체"),
    FRONTEND("프론트엔드"),
    BACKEND("백엔드"),
    DESIGNER("디자이너"),
    IOS("IOS"),
    ANDROID("안드로이드"),
    DEVOPS("데브옵스"),
    PRODUCT_MANAGER("PM"),
    MARKETER("마케터"),
    SERVICE_PLANNER("기획자");

    private final String korString;

    public static Role of(String korString) {
        return Arrays.stream(Role.values())
                .filter(role -> role.getKorString().equalsIgnoreCase(korString))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid role: " + korString));
    }
}
