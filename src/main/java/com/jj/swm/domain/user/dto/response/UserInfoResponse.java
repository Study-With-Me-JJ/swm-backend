package com.jj.swm.domain.user.dto.response;

import com.jj.swm.domain.user.entity.RoleType;
import com.jj.swm.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class UserInfoResponse {

    private UUID userId;
    private String profileImageUrl;
    private String nickname;
    private RoleType userRole;

    public static UserInfoResponse from(User user){
        return UserInfoResponse.builder()
                .userId(user.getId())
                .profileImageUrl(user.getProfileImageUrl())
                .nickname(user.getNickname())
                .userRole(user.getUserRole())
                .build();
    }
}
