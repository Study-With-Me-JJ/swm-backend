package com.jj.swm.domain.user.auth.dto.response;

import org.springframework.http.ResponseCookie;

public record Token(String accessToken, ResponseCookie refreshToken) {
}
