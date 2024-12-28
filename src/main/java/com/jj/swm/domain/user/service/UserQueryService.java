package com.jj.swm.domain.user.service;

import com.jj.swm.domain.user.repository.UserCredentialRepository;
import com.jj.swm.domain.user.repository.UserRepository;
import com.jj.swm.global.common.enums.ErrorCode;
import com.jj.swm.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserQueryService {

    private final UserRepository userRepository;
    private final UserCredentialRepository userCredentialRepository;

    public void validateLoginId(String loginId) {
        boolean loginIdDuplicatedStatus = userCredentialRepository.existsByLoginId(loginId);

        if (loginIdDuplicatedStatus) {
            throw new GlobalException(ErrorCode.NOT_VALID, "duplicated loginId");
        }
    }

    public void validateNickname(String nickname) {
        boolean nicknameDuplicatedStatus = userRepository.existsByNickname(nickname);

        if (nicknameDuplicatedStatus) {
            throw new GlobalException(ErrorCode.NOT_VALID, "duplicated nickname");
        }
    }
}
