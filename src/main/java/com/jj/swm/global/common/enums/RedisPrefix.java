package com.jj.swm.global.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RedisPrefix {
    EMAIL_AUTH_CODE("email_auth_code:"),
    PASSWORD_AUTH_CODE("password_auth_code:"),
    ID_AUTH_CODE("id_auth_code:");

    private final String value;
}
