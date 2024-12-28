package com.jj.swm.global.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExpirationTime {
    EMAIL(600000),
    EMAIL_VERIFIED(1800000);

    private final long value;

}
