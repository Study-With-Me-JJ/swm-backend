package com.jj.swm.domain.user.core.dto.component;

import com.jj.swm.domain.user.core.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class UserInfo {

    private UUID userId;

    private String profileImageUrl;

    private String nickname;

    public static UserInfo from(User user) {
        return UserInfo.builder()
                .userId(user.getId())
                .profileImageUrl(user.getProfileImageUrl())
                .nickname(user.getNickname())
                .build();
    }
}
