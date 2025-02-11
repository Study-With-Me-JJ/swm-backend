package com.jj.swm.global.security.custom;

import com.jj.swm.domain.user.core.entity.User;
import com.jj.swm.domain.user.core.entity.UserCredential;
import com.jj.swm.domain.user.core.repository.UserCredentialRepository;
import com.jj.swm.domain.user.core.repository.UserRepository;
import com.jj.swm.global.security.oauth.OAuth2UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final UserCredentialRepository userCredentialRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1. 유저 정보(attributes) 가져오기
        Map<String, Object> oAuth2UserAttributes = super.loadUser(userRequest).getAttributes();

        // 2. registrationId 가져오기 (third-party id)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        System.out.println("registrationId : " + registrationId);
        // 3. userNameAttributeName 가져오기
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();

        // 4. 유저 정보 dto 생성
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfo.of(registrationId, oAuth2UserAttributes, userNameAttributeName);

        // 5. 회원가입 및 로그인
        User user = getOrSave(oAuth2UserInfo);

        // 6. OAuth2User로 반환
        return new CustomUserDetails(user, oAuth2UserAttributes, userNameAttributeName);
    }

    @Transactional
    public User getOrSave(OAuth2UserInfo oAuth2UserInfo) {
        UserCredential userCredential = userCredentialRepository.findByValue(oAuth2UserInfo.value())
                .orElseGet(() -> {
                    User user = oAuth2UserInfo.toUserEntity();
                    userRepository.save(user);
                    return oAuth2UserInfo.toUserCredentialEntity(user, oAuth2UserInfo.provider());
                });

        userCredentialRepository.save(userCredential);

        return userCredential.getUser();
    }
}
