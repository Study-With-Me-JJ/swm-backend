package com.jj.swm.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CustomUserCreateRequest {

    @NotBlank
    private String nickname;

    private String profileImageUrl;

    @NotBlank
    private String password;

    @Email
    @NotBlank
    private String loginId;

    public void updatePasswordWithEncryption(String encryptedPassword) {
        this.password = encryptedPassword;
    }
}
