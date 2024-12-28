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

    @PostMapping("/v1/user/login-id/verification")
    public ApiResponse<Boolean> verifyAuthCodeForLoginId(
            @Email @NotBlank @RequestParam("loginId") String loginId, @RequestParam("authCode") String authCode
    ) {
        userCommandService.verifyAuthCode(loginId, authCode);

        return ApiResponse.ok(true);
    }

    @PostMapping("/v1/custom/user")
    public ApiResponse<Void> createCustomUser(@RequestBody CustomUserCreateRequest createRequest){
        userCommandService.createCustomUser(createRequest);

        return ApiResponse.created(null);
    }
}
