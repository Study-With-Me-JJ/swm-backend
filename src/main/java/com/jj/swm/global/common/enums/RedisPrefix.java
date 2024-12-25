package com.jj.swm.global.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RedisPrefix {
    AUTH_CODE("auth_code:");

    private final String value;
}
