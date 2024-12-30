package com.jj.swm.global.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AllowedPaths {
    LOGIN("/login/**"),
    AUTH_LOGIN("/api/auth/login/**"),
    REISSUE("/api/auth/reissue/**"),
    HEALTH_CHECK("/api/health/**"),
    USER("/api/v1/user/**"),

    SWAGGER("/swagger-ui.html"),
    FAVICON("/favicon.ico");

    private final String path;

    public static String[] getAllowedPaths() {
        return new String[] {
            LOGIN.path,
            AUTH_LOGIN.path,
            REISSUE.path,
            HEALTH_CHECK.path,
            USER.path,
            FAVICON.path,
            SWAGGER.path
        };
    }
}
