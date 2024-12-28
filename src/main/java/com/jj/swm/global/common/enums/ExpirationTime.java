package com.jj.swm.global.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExpirationTime {
    EMAIL(600000), // 10분
    EMAIL_VERIFIED(1800000); // 30분

    private final long value;

}
