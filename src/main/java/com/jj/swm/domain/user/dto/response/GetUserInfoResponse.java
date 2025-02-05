package com.jj.swm.domain.user.dto.response;

import com.jj.swm.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class GetUserInfoResponse {

    private UUID userId;
    private String nickname;
    private String name;
    private String profileImageUrl;

    public static GetUserInfoResponse from(User user) {
        return GetUserInfoResponse.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .name(user.getName())
                .profileImageUrl(user.getProfileImageUrl())
                .build();
    }
}
