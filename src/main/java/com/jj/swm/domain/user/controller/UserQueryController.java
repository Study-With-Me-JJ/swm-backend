package com.jj.swm.domain.user.controller;

import com.jj.swm.domain.user.dto.response.GetBusinessVerificationRequestResponse;
import com.jj.swm.domain.user.dto.response.GetUserInfoResponse;
import com.jj.swm.domain.user.entity.InspectionStatus;
import com.jj.swm.domain.user.service.UserQueryService;
import com.jj.swm.global.common.dto.ApiResponse;
import com.jj.swm.global.common.dto.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Tag(name = "User", description = "<b>[유저]</b> API")
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

    @GetMapping("/v1/user")
    public ApiResponse<GetUserInfoResponse> getUserInfo(Principal principal){
        GetUserInfoResponse response
                = userQueryService.getUserInfo(UUID.fromString(principal.getName()));

        return ApiResponse.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/v1/user/business/verification/requests")
    public ApiResponse<PageResponse<GetBusinessVerificationRequestResponse>> getBusinessVerificationRequests(
            @RequestParam(
                    value = "pageNo",
                    required = false,
                    defaultValue = "0"
            ) int pageNo,
            @RequestParam(
                    value = "status",
                    required = false,
                    defaultValue = "PENDING, APPROVED, REJECTED"
            ) List<InspectionStatus> status
    ) {
        PageResponse<GetBusinessVerificationRequestResponse> response
                = userQueryService.getBusinessVerificationRequests(status, pageNo);

        return ApiResponse.ok(response);
    }

    @GetMapping("/health")
    public ApiResponse<String> healthCheck() {
        return ApiResponse.ok("OK");
    }
}
