package com.jj.swm.global.security.oauth;

import com.jj.swm.domain.user.entity.Provider;
import com.jj.swm.domain.user.entity.RoleType;
import com.jj.swm.domain.user.entity.User;
import com.jj.swm.domain.user.entity.UserCredential;
import com.jj.swm.global.common.enums.ErrorCode;
import com.jj.swm.global.exception.auth.AuthException;
import lombok.Builder;

import java.util.Map;
import java.util.UUID;

@Builder
public record OAuth2UserInfo(
        String value,
        String email,
        String profile,
        Provider provider
) {
    public static OAuth2UserInfo of(String registrationId, Map<String, Object> attributes, String userNameAttributeName) {
        return switch (registrationId) { // registration id별로 userInfo 생성
            case "google" -> ofGoogle(attributes, userNameAttributeName);
            case "kakao" -> ofKakao(attributes, userNameAttributeName);
            case "github" -> ofGithub(attributes, userNameAttributeName);
            case "naver" -> ofNaver(attributes, userNameAttributeName);
            default -> throw new AuthException(ErrorCode.NOT_VALID, "invalid registrationId");
        };
    }

    private static OAuth2UserInfo ofGoogle(Map<String, Object> attributes, String userNameAttributeName) {
        return OAuth2UserInfo.builder()
                .value("google_" + attributes.get(userNameAttributeName).toString())
                .email((String) attributes.get("email"))
                .profile((String) attributes.get("picture"))
                .provider(Provider.GOOGLE)
                .build();
    }

    private static OAuth2UserInfo ofKakao(Map<String, Object> attributes, String userNameAttributeName) {
        Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) account.get("profile");

        return OAuth2UserInfo.builder()
                .value("kakao_" + attributes.get(userNameAttributeName).toString())
                .email((String) account.get("email"))
                .profile((String) profile.get("profile_image_url"))
                .provider(Provider.KAKAO)
                .build();
    }

    private static OAuth2UserInfo ofGithub(Map<String, Object> attributes, String userNameAttributeName) {
        return OAuth2UserInfo.builder()
                .value("github_" +attributes.get(userNameAttributeName).toString())
                .email((String) attributes.get("email"))
                .profile((String) attributes.get("picture"))
                .provider(Provider.GITHUB)
                .build();
    }

    private static OAuth2UserInfo ofNaver(Map<String, Object> attributes, String userNameAttributeName) {
        return OAuth2UserInfo.builder()
                .value("naver_" + attributes.get(userNameAttributeName).toString())
                .email((String) attributes.get("email"))
                .profile((String) attributes.get("picture"))
                .provider(Provider.NAVER)
                .build();
    }

    public User toUserEntity() {
        return User.builder()
                .id(UUID.randomUUID())
                .nickname(value)
                .profileImageUrl(profile)
                .userRole(RoleType.USER)
                .build();
    }

    public UserCredential toUserCredentialEntity(User user, Provider provider) {
        return UserCredential.builder()
                .loginId(email)
                .provider(provider)
                .user(user)
                .value(value)
                .build();
    }
}
