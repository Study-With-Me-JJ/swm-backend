package com.jj.swm.domain.auth.controller;

import com.jj.swm.domain.auth.dto.Token;
import com.jj.swm.domain.auth.dto.request.LoginRequest;
import com.jj.swm.domain.auth.dto.RefreshToken;
import com.jj.swm.domain.auth.service.AuthService;
import com.jj.swm.global.common.dto.ApiResponse;
import com.jj.swm.global.security.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ApiResponse<RefreshToken> login(
            @Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        Token token = authService.login(request);
        response.addHeader("Authorization", JwtProvider.BEARER + token.accessToken());

        return ApiResponse.ok(RefreshToken.from(token.refreshToken()));
    }

    @PatchMapping("/logout")
    public ApiResponse<Void> logout(@RequestHeader(AUTHORIZATION) String authorization) {
        authService.logout(authorization);

        return ApiResponse.ok(null);
    }

    @PatchMapping("/reissue")
    public ApiResponse<Void> reissue(@Valid @RequestBody Token token, HttpServletResponse response) {
        Token newToken = authService.reissue(token);
        response.addHeader("Authorization", JwtProvider.BEARER + newToken.accessToken());

        return ApiResponse.ok(null);
    }
}
