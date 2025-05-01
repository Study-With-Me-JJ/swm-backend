package com.jj.swm.domain.user.core.dto.request;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VerifyAuthCodeRequest {

    private String loginId;
    private String authCode;
}
