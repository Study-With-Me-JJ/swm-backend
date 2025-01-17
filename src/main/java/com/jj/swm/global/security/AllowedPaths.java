package com.jj.swm.global.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum AllowedPaths {
    LOGIN("/login/**"),
    AUTH_LOGIN("/api/auth/login/**"),
    REISSUE("/api/auth/reissue/**"),
    HEALTH_CHECK("/api/health/**"),
    USER("/api/v1/user/**"),
    SWAGGER_API_DOCS("/v3/api-docs/**"),
    SWAGGER_INDEX("/swagger-ui/**"),
    EXTERNAL_API("/api/v1/external/**"),
    FAVICON("/favicon.ico");

    private final String path;

    public static String[] getAllowedPaths() {
        return Arrays.stream(AllowedPaths.values()).map(AllowedPaths::getPath).toArray(String[]::new);
    }
}
