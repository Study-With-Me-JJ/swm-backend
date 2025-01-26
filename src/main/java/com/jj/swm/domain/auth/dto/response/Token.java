package com.jj.swm.domain.auth.dto.response;

import org.springframework.http.ResponseCookie;

public record Token(String accessToken, ResponseCookie refreshToken) {
}
