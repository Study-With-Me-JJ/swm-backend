package com.jj.swm.domain.user.controller;

import com.jj.swm.domain.user.service.UserQueryService;
import com.jj.swm.global.common.dto.ApiResponse;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserQueryController {

    private final UserQueryService userQueryService;

    @GetMapping("/v1/user/login-id/validation")
    public ApiResponse<Boolean> validateUserLoginId(@Email @NotBlank @RequestParam("loginId") String loginId) {
        boolean response = userQueryService.validateLoginId(loginId);

        return ApiResponse.ok(response);
    }

    @GetMapping("/v1/user/nickname/validation")
    public ApiResponse<Boolean> validateUserNickname(@RequestParam("nickname") String nickname) {
        boolean response = userQueryService.validateNickname(nickname);

        return ApiResponse.ok(response);
    }

    @GetMapping("/health")
    public ApiResponse<String> healthCheck() {
        return ApiResponse.ok("OK");
    }
}
