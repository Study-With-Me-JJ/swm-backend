package com.jj.swm.domain.auth.service;

import com.jj.swm.domain.auth.dto.request.LoginRequest;
import com.jj.swm.domain.auth.dto.response.Token;
import com.jj.swm.domain.user.entity.UserCredential;
import com.jj.swm.domain.user.repository.UserCredentialRepository;
import com.jj.swm.global.common.enums.ErrorCode;
import com.jj.swm.global.exception.GlobalException;
import com.jj.swm.global.security.jwt.JwtProvider;
import com.jj.swm.global.security.jwt.TokenRedisService;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final UserCredentialRepository credentialRepository;
    private final TokenRedisService tokenRedisService;

    @Transactional
    public Token login(LoginRequest request) {
        UserCredential credential = credentialRepository.findByLoginId(request.getLoginId())
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "User not found"));

        if (!passwordEncoder.matches(request.getPassword(), credential.getValue())) {
            throw new GlobalException(ErrorCode.FORBIDDEN, "Password mismatch");
        }

        return jwtProvider.generateTokens(credential.getUser());
    }

    @Transactional
    public void logout(String authorization) {
        String accessToken = jwtProvider.resolveAccessToken(authorization);

        String userId = jwtProvider.getUserSubject(accessToken);

        tokenRedisService.deleteByUserId(userId);
        tokenRedisService.saveAccessTokenForLogout(accessToken);
    }

    public Token reissue(Cookie[] cookies) {
        return jwtProvider.reissue(cookies);
    }
}
