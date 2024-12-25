package com.jj.swm.domain.user.controller;

import com.jj.swm.domain.user.dto.CustomUserCreateRequest;
import com.jj.swm.domain.user.service.UserCommandService;
import com.jj.swm.global.common.dto.ApiResponse;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserCommandController {

    private final UserCommandService userCommandService;

    @PostMapping("/v1/user/login-id/auth")
    public ApiResponse<Void> sendAuthCodeForLoginId(@Email @NotBlank @RequestParam("loginId") String loginId) {
        userCommandService.sendAuthCode(loginId);

        return ApiResponse.ok(null);
    }

    //상태코드로 정삭 여부 판별 -> 바디값 없음 ( 수정 원하면 수정 )
    @PostMapping("/v1/user/login-id/verification")
    public ApiResponse<Void> verifyAuthCodeForLoginId(
            @Email @NotBlank @RequestParam("loginId") String loginId, @RequestParam("authCode") String authCode
    ) {
        userCommandService.verifyAuthCode(loginId, authCode);

        return ApiResponse.ok(null);
    }

    @PostMapping("/v1/custom/user")
    public ApiResponse<Void> createCustomUser(@RequestBody CustomUserCreateRequest createRequest){
        userCommandService.createCustomUser(createRequest);

        return ApiResponse.created(null);
    }
}
