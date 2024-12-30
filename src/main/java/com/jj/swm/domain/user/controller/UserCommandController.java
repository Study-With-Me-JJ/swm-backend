package com.jj.swm.domain.user.controller;

import com.jj.swm.domain.user.dto.CustomUserCreateRequest;
import com.jj.swm.domain.user.dto.request.SendAuthCodeRequest;
import com.jj.swm.domain.user.dto.request.VerifyAuthCodeRequest;
import com.jj.swm.domain.user.service.UserCommandService;
import com.jj.swm.global.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserCommandController {

    private final UserCommandService userCommandService;

    @PostMapping("/v1/user/login-id/auth")
    public ApiResponse<Void> sendAuthCodeForLoginId(@Valid @RequestBody SendAuthCodeRequest request) {
        userCommandService.sendAuthCode(request.getLoginId());

        return ApiResponse.ok(null);
    }

    @PostMapping("/v1/user/login-id/verification")
    public ApiResponse<Boolean> verifyAuthCodeForLoginId(@Valid @RequestBody VerifyAuthCodeRequest request) {
        boolean response = userCommandService.verifyAuthCode(request.getLoginId(), request.getAuthCode());

        return ApiResponse.ok(response);
    }

    @PostMapping("/v1/user/custom")
    public ApiResponse<Void> createCustomUser(@RequestBody CustomUserCreateRequest createRequest){
        userCommandService.createCustomUser(createRequest);

        return ApiResponse.created(null);
    }
}
