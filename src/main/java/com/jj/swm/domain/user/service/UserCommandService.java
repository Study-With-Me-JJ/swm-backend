package com.jj.swm.domain.user.service;

import com.jj.swm.domain.user.dto.event.BusinessVerificationRequestEvent;
import com.jj.swm.domain.user.dto.request.*;
import com.jj.swm.domain.user.entity.BusinessVerificationRequest;
import com.jj.swm.domain.user.entity.User;
import com.jj.swm.domain.user.entity.UserCredential;
import com.jj.swm.domain.user.repository.BusinessVerificationRequestRepository;
import com.jj.swm.domain.user.repository.UserCredentialRepository;
import com.jj.swm.domain.user.repository.UserRepository;
import com.jj.swm.global.common.enums.EmailSendType;
import com.jj.swm.global.common.enums.ErrorCode;
import com.jj.swm.global.common.enums.ExpirationTime;
import com.jj.swm.global.common.enums.RedisPrefix;
import com.jj.swm.global.common.service.EmailService;
import com.jj.swm.global.common.service.RedisService;
import com.jj.swm.global.common.util.RandomUtils;
import com.jj.swm.global.event.Events;
import com.jj.swm.global.exception.GlobalException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
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
    private final BusinessVerificationRequestRepository businessVerificationRequestRepository;

    public void sendAuthCode(String loginId, EmailSendType type) {
        String authCode = RandomUtils.generateRandomCode();

        String redisPrefix = getRedisPrefix(type);

        emailService.sendAuthCodeEmail(loginId, authCode, type).thenAccept(success -> {
            if (success) {
                redisService.setValueWithExpiration(
                        redisPrefix + loginId,
                        authCode,
                        ExpirationTime.EMAIL.getValue()
                );
            }
        });
    }

    public boolean verifyAuthCode(
            String loginId,
            String authCode,
            EmailSendType type
    ) {
        String redisPrefix = getRedisPrefix(type);

        String storedAuthCode = redisService.getValue(redisPrefix + loginId);

        if (!authCode.equals(storedAuthCode)) {
            return false;
        } else {
            redisService.setValueWithExpiration(
                    redisPrefix + loginId,
                    AUTH_CODE_VERIFIED,
                    ExpirationTime.EMAIL_VERIFIED.getValue()
            );
            return true;
        }
    }

    @Transactional
    public void create(CreateUserRequest createRequest) {
        validateNicknameAndLoginId(createRequest.getNickname(), createRequest.getLoginId());

        User user = User.from(createRequest);
        userRepository.save(user);

        String storedAuthCode
                = redisService.getValue(RedisPrefix.EMAIL_AUTH_CODE.getValue() + createRequest.getLoginId());

        if (!AUTH_CODE_VERIFIED.equals(storedAuthCode)) {
            throw new GlobalException(ErrorCode.FORBIDDEN, "Unverified Auth Code");
        }

        String encryptedPassword = passwordEncoder.encode(createRequest.getPassword());
        createRequest.updatePasswordWithEncryption(encryptedPassword);

        UserCredential userCredential = UserCredential.of(user, createRequest);
        userCredentialRepository.save(userCredential);
    }

    @Transactional
    public void update(UpdateUserRequest request, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_VALID, "Not Found User"));

        if(!userRepository.existsByNickname(request.getNickname())){
            user.modify(request);
        } else{
            throw new GlobalException(ErrorCode.NOT_VALID, "duplicated nickname");
        }
    }

    @Transactional
    public void updateUserPassword(UpdateUserPasswordRequest request, UUID userId) {
        UserCredential userCredential = userCredentialRepository.findByLoginId(request.getLoginId())
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_VALID, "Not Found User"));

        if (userId == null) {
            String storedAuthCode
                    = redisService.getValue(RedisPrefix.PASSWORD_AUTH_CODE.getValue() + request.getLoginId());

            if (!AUTH_CODE_VERIFIED.equals(storedAuthCode)) {
                throw new GlobalException(ErrorCode.FORBIDDEN, "Unverified Auth Code");
            }
        }

        String encryptedPassword = passwordEncoder.encode(request.getPassword());

        userCredential.modifyPassword(encryptedPassword);

        // 로그인한 유저인 경우 프론트에게 로그아웃 요청
    }

    public void retrieveLoginId(RetrieveUserLoginIdRequest request) {
        if(userCredentialRepository
                .existsByLoginIdAndName(request.getLoginId(), request.getName())
        ) {
            emailService.sendRetrieveEmail(request.getLoginId());
        }
    }

    @Transactional
    public void validateBusinessStatus(UpgradeRoomAdminRequest request, UUID userId) {
        if (!businessStatusService.validateBusinessStatus(request)) {
            throw new GlobalException(ErrorCode.NOT_VALID, "국세청에 등록되지 않은 사업자등록번호입니다.");
        } else {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "User Not Found"));

            if(businessVerificationRequestRepository
                    .existsByBusinessNumber(request.getBusinessNumber())
            ) {
                throw new GlobalException(ErrorCode.NOT_VALID, "해당 사업자 번호는 요청되어 있습니다.");
            }

            BusinessVerificationRequest businessVerificationRequest = BusinessVerificationRequest.of(user, request);
            businessVerificationRequestRepository.save(businessVerificationRequest);

            Events.send(BusinessVerificationRequestEvent.from(businessVerificationRequest));
        }
    }

    @Transactional
    public void updateInspectionStatusApproval(List<Long> businessVerificationRequestIds) {
        if(businessVerificationRequestRepository.countByIdIn(businessVerificationRequestIds)
                == businessVerificationRequestIds.size()
        ) {
            businessVerificationRequestRepository.updateInspectionStatusApproval(businessVerificationRequestIds);
        } else {
            throw new GlobalException(ErrorCode.NOT_VALID, "Invalid Match Id");
        }
    }

    @Transactional
    public void updateInspectionStatusRejection(List<Long> businessVerificationRequestIds) {
        if(businessVerificationRequestRepository.countByIdIn(businessVerificationRequestIds)
                == businessVerificationRequestIds.size()
        ) {
            businessVerificationRequestRepository.updateInspectionStatusRejection(businessVerificationRequestIds);
        } else {
            throw new GlobalException(ErrorCode.NOT_VALID, "Invalid Match Id");
        }
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

    private String getRedisPrefix(EmailSendType type) {
        if (Objects.requireNonNull(type) == EmailSendType.PASSWORD) {
            return RedisPrefix.PASSWORD_AUTH_CODE.getValue();
        }
        return RedisPrefix.EMAIL_AUTH_CODE.getValue();
    }
}
