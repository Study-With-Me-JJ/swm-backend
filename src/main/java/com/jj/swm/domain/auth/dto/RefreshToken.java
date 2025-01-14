package com.jj.swm.domain.auth.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken{

    private String refreshToken;

    public static RefreshToken from(String refreshToken) {
        return RefreshToken.builder()
                .refreshToken(refreshToken)
                .build();
    }
}
