package com.jj.swm.domain.user.service;

import com.jj.swm.domain.user.dto.CustomUserCreateRequest;
import com.jj.swm.domain.user.entity.User;
import com.jj.swm.domain.user.entity.UserCredential;
import com.jj.swm.domain.user.repository.UserCredentialRepository;
import com.jj.swm.domain.user.repository.UserRepository;
import com.jj.swm.global.common.enums.ErrorCode;
import com.jj.swm.global.common.enums.ExpirationTime;
import com.jj.swm.global.common.enums.RedisPrefix;
import com.jj.swm.global.common.service.EmailService;
import com.jj.swm.global.common.service.RedisService;
import com.jj.swm.global.common.util.RandomUtils;
import com.jj.swm.global.exception.GlobalException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserCommandService {

    private static final String AUTH_CODE_VERIFIED = "VERIFIED";

    private final RedisService redisService;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserQueryService userQueryService;
    private final UserCredentialRepository userCredentialRepository;

    public void sendAuthCode(String loginId) {
        String randomCode = RandomUtils.generateRandomCode();

        redisService.setValueWithExpiration(
                RedisPrefix.AUTH_CODE.getValue() + loginId,
                randomCode,
                ExpirationTime.EMAIL.getValue()
        );

        emailService.sendEmail(loginId, randomCode);
    }

    public void verifyAuthCode(String loginId, String authCode) {
        String storedAuthCode = redisService.getValue(RedisPrefix.AUTH_CODE.getValue() + loginId);

        if (!authCode.equals(storedAuthCode)) {
            throw new GlobalException(ErrorCode.FORBIDDEN, "auth code mismatch");
        }

        redisService.setValueWithExpiration(
                RedisPrefix.AUTH_CODE.getValue() + loginId,
                AUTH_CODE_VERIFIED,
                ExpirationTime.EMAIL_VERIFIED.getValue()
        );
    }

    @Transactional
    public void createCustomUser(CustomUserCreateRequest createRequest) {
        userQueryService.validateNickname(createRequest.getNickname());

        User user = User.from(createRequest);
        userRepository.save(user);

        userQueryService.validateLoginId(createRequest.getLoginId());
        String storedAuthCode = redisService.getValue(RedisPrefix.AUTH_CODE.getValue() + createRequest.getLoginId());

        if (!AUTH_CODE_VERIFIED.equals(storedAuthCode)) {
            throw new GlobalException(ErrorCode.FORBIDDEN, "auth code mismatch");
        }

        String encryptedPassword = passwordEncoder.encode(createRequest.getPassword());
        createRequest.updatePasswordWithEncryption(encryptedPassword);

        UserCredential userCredential = UserCredential.of(user, createRequest);
        userCredentialRepository.save(userCredential);
    }
}
