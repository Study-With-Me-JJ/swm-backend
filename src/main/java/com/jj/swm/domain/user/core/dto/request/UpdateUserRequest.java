package com.jj.swm.domain.user.core.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateUserRequest {

    @NotBlank
    private String nickname;

    private String profileImageUrl;

    @NotBlank
    private String name;
}
