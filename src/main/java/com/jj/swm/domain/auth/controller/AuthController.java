package com.jj.swm.domain.auth.controller;

import com.jj.swm.domain.auth.dto.request.LoginRequest;
import com.jj.swm.domain.auth.dto.response.Token;
import com.jj.swm.domain.auth.service.AuthService;
import com.jj.swm.global.common.dto.ApiResponse;
import com.jj.swm.global.security.jwt.JwtProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Tag(name = "Auth", description = "<b>[인증]</b> API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "Auth 로그인",
            description = "유저 계정으로 로그인합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "성공"
    )
    @PostMapping("/login")
    public ApiResponse<Void> login(
            @Valid @RequestBody LoginRequest request, HttpServletResponse httpServletResponse
    ) {
        Token token = authService.login(request);

        httpServletResponse.addHeader(AUTHORIZATION, JwtProvider.BEARER + token.accessToken());
        httpServletResponse.addHeader(HttpHeaders.SET_COOKIE, token.refreshToken().toString());

        return ApiResponse.ok(null);
    }

    @Operation(
            summary = "Auth 로그아웃",
            description = "로그아웃 합니다. 리프레시 토큰 삭제 요망"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "성공"
    )
    @PatchMapping("/logout")
    public ApiResponse<Void> logout(
            @RequestHeader(AUTHORIZATION) String authorization
    ) {
        authService.logout(authorization);

        return ApiResponse.ok(null);
    }

    @Operation(
            summary = "토큰 재발급",
            description = "토큰을 재발급합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "성공"
    )
    @PatchMapping("/reissue")
    public ApiResponse<Void> reissue(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        Token token = authService.reissue(httpServletRequest.getCookies());

        httpServletResponse.addHeader(AUTHORIZATION, JwtProvider.BEARER + token.accessToken());
        httpServletResponse.addHeader(HttpHeaders.SET_COOKIE, token.refreshToken().toString());

        return ApiResponse.ok(null);
    }
}
