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

    public boolean validateLoginId(String loginId) {
        return userCredentialRepository.existsByLoginId(loginId);
    }

    public boolean validateNickname(String nickname) {
        return userRepository.existsByNickname(nickname);
    }
}
