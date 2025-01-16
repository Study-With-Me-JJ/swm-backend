package com.jj.swm.domain.user.controller;

import com.jj.swm.domain.user.dto.request.CreateUserRequest;
import com.jj.swm.domain.user.dto.request.*;
import com.jj.swm.domain.user.service.UserCommandService;
import com.jj.swm.global.common.dto.ApiResponse;
import com.jj.swm.global.common.enums.EmailSendType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserCommandController {

    private final UserCommandService userCommandService;

    @PostMapping("/v1/user/login-id/auth-codes")
    public ApiResponse<Void> sendAuthCodeForLoginId(@Valid @RequestBody SendAuthCodeRequest request) {
        userCommandService.sendAuthCode(request.getLoginId(), EmailSendType.EMAIL);

        return ApiResponse.ok(null);
    }

    @PostMapping("/v1/user/login-id/auth-codes/verification")
    public ApiResponse<Boolean> verifyAuthCodeForLoginId(@Valid @RequestBody VerifyAuthCodeRequest request) {
        boolean response = userCommandService.verifyAuthCode(
                request.getLoginId(),
                request.getAuthCode(),
                EmailSendType.EMAIL
        );

        return ApiResponse.ok(response);
    }

    @PostMapping("/v1/user/password/auth-codes")
    public ApiResponse<Void> sendAuthCodeForPassword(@Valid @RequestBody SendAuthCodeRequest request){
        userCommandService.sendAuthCode(request.getLoginId(), EmailSendType.PASSWORD);

        return ApiResponse.ok(null);
    }

    @PostMapping("/v1/user/password/auth-codes/verification")
    public ApiResponse<Boolean> verifyAuthCodeForPassword(@Valid @RequestBody VerifyAuthCodeRequest request){
        boolean response = userCommandService.verifyAuthCode(
                request.getLoginId(),
                request.getAuthCode(),
                EmailSendType.PASSWORD
        );

        return ApiResponse.ok(response);
    }

    @PostMapping("/v1/user")
    public ApiResponse<Void> create(@Valid @RequestBody CreateUserRequest request){
        userCommandService.create(request);

        return ApiResponse.created(null);
    }

    @PatchMapping("/v1/user")
    public ApiResponse<Void> update(
            @Valid @RequestBody UpdateUserRequest request, Principal principal){
        userCommandService.update(request,UUID.fromString(principal.getName()));

        return ApiResponse.ok(null);
    }


    @PatchMapping("/v1/user/password")
    public ApiResponse<Void> updatePassword(
            @Valid @RequestBody UpdateUserPasswordRequest request, Principal principal
    ){
        userCommandService.updateUserPassword(
                request, principal != null ? UUID.fromString(principal.getName()) : null);

        return ApiResponse.ok(null);
    }

    @PostMapping("/v1/user/login-id/retrieval")
    public ApiResponse<Void> retrieveLoginId(@Valid @RequestBody RetrieveUserLoginIdRequest request){
        userCommandService.retrieveLoginId(request);

        return ApiResponse.ok(null);
    }

    @PatchMapping("/v1/business/user/validation")
    public ApiResponse<Void> validateBusinessStatus(
            @Valid @RequestBody UpgradeRoomAdminRequest request, Principal principal) {
        userCommandService.validateBusinessStatus(request, UUID.fromString(principal.getName()));

        return ApiResponse.ok(null);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/v1/user/business/verification/requests/approval")
    public ApiResponse<Void> updateInspectionStatusApproval(
        @Valid @RequestBody UpdateInspectionStatusRequest request
    ) {
        userCommandService.updateInspectionStatusApproval(request.getBusinessVerificationRequestIds());

        return ApiResponse.ok(null);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/v1/user/business/verification/requests/rejection")
    public ApiResponse<Void> updateInspectionStatusRejection(
            @Valid @RequestBody UpdateInspectionStatusRequest request
    ) {
        userCommandService.updateInspectionStatusRejection(request.getBusinessVerificationRequestIds());

        return ApiResponse.ok(null);
    }
}
