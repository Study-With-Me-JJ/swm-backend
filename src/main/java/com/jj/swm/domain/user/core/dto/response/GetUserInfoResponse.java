package com.jj.swm.domain.user.core.dto.response;

import com.jj.swm.domain.user.core.entity.User;
import com.jj.swm.domain.user.core.entity.UserCredential;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class GetUserInfoResponse {

    private UUID userId;
    private String nickname;
    private String name;
    private String email;
    private String profileImageUrl;

    public static GetUserInfoResponse from(UserCredential userCredential) {
        User user = userCredential.getUser();

        return GetUserInfoResponse.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .name(user.getName())
                .email(userCredential.getLoginId())
                .profileImageUrl(user.getProfileImageUrl())
                .build();
    }
}
