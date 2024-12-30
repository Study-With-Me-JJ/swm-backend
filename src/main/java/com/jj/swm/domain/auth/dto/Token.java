package com.jj.swm.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record Token(String accessToken, String refreshToken) {
}
