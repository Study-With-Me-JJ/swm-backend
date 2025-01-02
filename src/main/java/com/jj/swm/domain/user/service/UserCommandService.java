package com.jj.swm.domain.user.service;

import com.jj.swm.domain.user.dto.CustomUserCreateRequest;
import com.jj.swm.domain.user.dto.request.UpgradeRoomAdminRequest;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserCommandService {

    private static final String AUTH_CODE_VERIFIED = "VERIFIED";

    private final RedisService redisService;
    private final EmailService emailService;
    private final BusinessStatusService businessStatusService;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserCredentialRepository userCredentialRepository;

    public void sendAuthCode(String loginId) {
        String randomCode = RandomUtils.generateRandomCode();

        emailService.sendEmail(loginId, randomCode).thenAccept(success -> {
            if(success) {
                redisService.setValueWithExpiration(
                        RedisPrefix.AUTH_CODE.getValue() + loginId,
                        randomCode,
                        ExpirationTime.EMAIL.getValue()
                );
            }
        });
    }

    public boolean verifyAuthCode(String loginId, String authCode) {
        String storedAuthCode = redisService.getValue(RedisPrefix.AUTH_CODE.getValue() + loginId);

        if (!authCode.equals(storedAuthCode)) {
            return false;
        } else {
            redisService.setValueWithExpiration(
                    RedisPrefix.AUTH_CODE.getValue() + loginId,
                    AUTH_CODE_VERIFIED,
                    ExpirationTime.EMAIL_VERIFIED.getValue()
            );

            return true;
        }
    }

    @Transactional
    public void createCustomUser(CustomUserCreateRequest createRequest) {
        validateNicknameAndLoginId(createRequest.getNickname(), createRequest.getLoginId());

        User user = User.from(createRequest);
        userRepository.save(user);

        String storedAuthCode = redisService.getValue(RedisPrefix.AUTH_CODE.getValue() + createRequest.getLoginId());

        if (!AUTH_CODE_VERIFIED.equals(storedAuthCode)) {
            throw new GlobalException(ErrorCode.FORBIDDEN, "Unverified Auth Code");
        }

        String encryptedPassword = passwordEncoder.encode(createRequest.getPassword());
        createRequest.updatePasswordWithEncryption(encryptedPassword);

        UserCredential userCredential = UserCredential.of(user, createRequest);
        userCredentialRepository.save(userCredential);
    }

    private void validateNicknameAndLoginId(String nickname, String loginId) {
        boolean nicknameDuplicatedStatus = userRepository.existsByNickname(nickname);

        if (nicknameDuplicatedStatus) {
            throw new GlobalException(ErrorCode.NOT_VALID, "duplicated nickname");
        }

        boolean loginIdDuplicatedStatus = userCredentialRepository.existsByLoginId(loginId);

        if (loginIdDuplicatedStatus) {
            throw new GlobalException(ErrorCode.NOT_VALID, "duplicated loginId");
        }
    }

    @Transactional
    public void validateBusinessStatus(UpgradeRoomAdminRequest request, UUID userId) {
        if (!businessStatusService.validateBusinessStatus(request)) {
            throw new GlobalException(ErrorCode.NOT_VALID, "국세청에 등록되지 않은 사업자등록번호입니다.");
        } else {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "User Not Found"));

            user.modifyRoleRoomAdmin();
        }
    }
}
