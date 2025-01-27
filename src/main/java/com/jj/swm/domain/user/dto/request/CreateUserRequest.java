package com.jj.swm.domain.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateUserRequest {

    @NotBlank
    private String nickname;

    private String profileImageUrl;

    @NotBlank
    private String password;

    @NotBlank
    private String name;

    @Email
    @NotBlank
    private String loginId;

    public void updatePasswordWithEncryption(String encryptedPassword) {
        this.password = encryptedPassword;
    }
}
