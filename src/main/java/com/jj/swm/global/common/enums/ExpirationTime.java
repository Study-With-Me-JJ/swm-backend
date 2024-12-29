package com.jj.swm.global.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExpirationTime {
    EMAIL(600000), // 10분
    EMAIL_VERIFIED(30 * 60 * 1000L), // 30분
    ACCESS_TOKEN(30 * 60 * 1000L), // 30분
    REFRESH_TOKEN(60 * 60 * 3 * 1000L); // 3시간

    private final long value;
}
