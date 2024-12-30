package com.jj.swm.domain.user.dto;

import com.jj.swm.domain.user.entity.Provider;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Oauth2UserCreateRequest {

    @NotNull
    private Provider provider;

    @NotBlank
    private String providerId;
}
