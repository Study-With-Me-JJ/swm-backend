package com.jj.swm.domain.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateUserPasswordRequest {

    @Email
    @NotBlank
    private String loginId;

    private String oldPassword;

    @NotBlank
    private String newPassword;
}
