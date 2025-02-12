package com.jj.swm.domain.auth.controller;

import com.jj.swm.domain.auth.dto.request.LoginRequest;
import com.jj.swm.domain.auth.dto.response.Token;
import com.jj.swm.domain.auth.service.AuthService;
import com.jj.swm.global.common.dto.ApiResponse;
import com.jj.swm.global.security.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ApiResponse<Void> login(
            @Valid @RequestBody LoginRequest request, HttpServletResponse httpServletResponse
    ) {
        Token token = authService.login(request);

        httpServletResponse.addHeader(AUTHORIZATION, JwtProvider.BEARER + token.accessToken());
        httpServletResponse.addHeader(HttpHeaders.SET_COOKIE, token.refreshToken().toString());

        return ApiResponse.ok(null);
    }

    @PatchMapping("/logout")
    public ApiResponse<Void> logout(
            @RequestHeader(AUTHORIZATION) String authorization, HttpServletResponse httpServletResponse
    ) {
        authService.logout(authorization);

        return ApiResponse.ok(null);
    }

    @PatchMapping("/reissue")
    public ApiResponse<Void> reissue(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        Token token = authService.reissue(httpServletRequest.getCookies());

        httpServletResponse.addHeader(AUTHORIZATION, JwtProvider.BEARER + token.accessToken());
        httpServletResponse.addHeader(HttpHeaders.SET_COOKIE, token.refreshToken().toString());

        return ApiResponse.ok(null);
    }
}
