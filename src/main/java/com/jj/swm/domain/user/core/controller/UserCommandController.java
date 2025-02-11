package com.jj.swm.domain.user.core.controller;

import com.jj.swm.domain.user.core.dto.request.*;
import com.jj.swm.domain.user.core.entity.InspectionStatus;
import com.jj.swm.domain.user.core.service.UserCommandService;
import com.jj.swm.global.common.dto.ApiResponse;
import com.jj.swm.global.common.enums.EmailSendType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@Tag(name = "User", description = "<b>[유저]</b> API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserCommandController {

    private final UserCommandService userCommandService;

    @Operation(
            summary = "유저 생성(OAuth2 X)",
            description = "유저를 생성합니다. 유저의 이메일이 인증코드를 통해 검증되어 있어야 합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201", description = "성공"
    )
    @PostMapping("/v1/user")
    public ApiResponse<Void> create(@Valid @RequestBody CreateUserRequest request){
        userCommandService.create(request);

        return ApiResponse.created(null);
    }

    @Operation(
            summary = "유저 정보 수정",
            description = "유저를 정보를 수정합니다. 기존값은 추가해줘야 합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "성공"
    )
    @PatchMapping("/v1/user")
    public ApiResponse<Void> update(
            @Valid @RequestBody UpdateUserRequest request, Principal principal){
        userCommandService.update(request,UUID.fromString(principal.getName()));

        return ApiResponse.ok(null);
    }

    @Operation(
            summary = "유저 삭제(구현 X)",
            description = "유저를 삭제합니다. 관련된 모든 스터디, 스터디 룸도 삭제됩니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "성공"
    )
    @DeleteMapping("/v1/user")
    public ApiResponse<Void> delete(Principal principal){
        userCommandService.delete(UUID.fromString(principal.getName()));

        return ApiResponse.ok(null);
    }

    @Operation(
            summary = "유저 비밀번호 변경",
            description = "유저를 비밀번호를 변경합니다. " +
                    "비밀번호 찾기 유저의 경우는 newPassword만 필요(검증 필요)," +
                    "로그인 된 유저는 oldPassword, newPassword 입력시 변경 가능."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "성공"
    )
    @PatchMapping("/v1/user/password")
    public ApiResponse<Void> updatePassword(
            @Valid @RequestBody UpdateUserPasswordRequest request, Principal principal
    ){
        userCommandService.updateUserPassword(
                request, principal != null ? UUID.fromString(principal.getName()) : null);

        return ApiResponse.ok(null);
    }

    @Operation(
            summary = "유저 이메일 인증 코드 전송",
            description = "유저 이메일 인증 코드를 이메일에 전송합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "성공"
    )
    @PostMapping("/v1/user/login-id/auth-codes")
    public ApiResponse<Void> sendAuthCodeForLoginId(@Valid @RequestBody SendAuthCodeRequest request) {
        userCommandService.sendAuthCode(request.getLoginId(), EmailSendType.EMAIL);

        return ApiResponse.ok(null);
    }

    @Operation(
            summary = "유저 이메일 인증 코드 검증",
            description = "유저 이메일 인증 코드를 검증합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "성공"
    )
    @PostMapping("/v1/user/login-id/auth-codes/verification")
    public ApiResponse<Boolean> verifyAuthCodeForLoginId(@Valid @RequestBody VerifyAuthCodeRequest request) {
        boolean response = userCommandService.verifyAuthCode(
                request.getLoginId(),
                request.getAuthCode(),
                EmailSendType.EMAIL
        );

        return ApiResponse.ok(response);
    }

    @Operation(
            summary = "유저 패스워드 인증 코드 전송",
            description = "유저 패스워드 인증 코드를 이메일에 전송합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "성공"
    )
    @PostMapping("/v1/user/password/auth-codes")
    public ApiResponse<Void> sendAuthCodeForPassword(@Valid @RequestBody SendAuthCodeRequest request){
        userCommandService.sendAuthCode(request.getLoginId(), EmailSendType.PASSWORD);

        return ApiResponse.ok(null);
    }

    @Operation(
            summary = "유저 패스워드 인증 코드 검증",
            description = "유저 패스워드 인증 코드를 검증합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "성공"
    )
    @PostMapping("/v1/user/password/auth-codes/verification")
    public ApiResponse<Boolean> verifyAuthCodeForPassword(@Valid @RequestBody VerifyAuthCodeRequest request){
        boolean response = userCommandService.verifyAuthCode(
                request.getLoginId(),
                request.getAuthCode(),
                EmailSendType.PASSWORD
        );

        return ApiResponse.ok(response);
    }

    @Operation(
            summary = "유저 아이디 찾기",
            description = "등록된 유저 이메일로 아이디를 전송합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "성공"
    )
    @PostMapping("/v1/user/login-id/retrieval")
    public ApiResponse<Void> retrieveLoginId(@Valid @RequestBody RetrieveUserLoginIdRequest request){
        userCommandService.retrieveLoginId(request);

        return ApiResponse.ok(null);
    }

    @Operation(
            summary = "유저 스터디 룸 사업자 검수 요청",
            description = "유저 스터디 룸 사업자 검수를 요청합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "성공"
    )
    @PostMapping("/v1/business/user/validation")
    public ApiResponse<Void> validateBusinessStatus(
            @Valid @RequestBody UpgradeRoomAdminRequest request, Principal principal) {
        userCommandService.validateBusinessStatus(request, UUID.fromString(principal.getName()));

        return ApiResponse.ok(null);
    }

    @Operation(
            summary = "관리자 - 유저 스터디 룸 사업자 검수 요청 승인",
            description = "관리자가 유저 스터디 룸 사업자 검수 요청을 승인합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "성공"
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/v1/user/business/verification/requests/approval")
    public ApiResponse<Void> updateInspectionStatusApproval(
        @Valid @RequestBody UpdateInspectionStatusRequest request
    ) {
        userCommandService.updateInspectionStatus(request.getBusinessVerificationRequestIds(), InspectionStatus.APPROVED);

        return ApiResponse.ok(null);
    }

    @Operation(
            summary = "관리자 - 유저 스터디 룸 사업자 검수 요청 거부",
            description = "관리자가 유저 스터디 룸 사업자 검수 요청을 거부합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "성공"
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/v1/user/business/verification/requests/rejection")
    public ApiResponse<Void> updateInspectionStatusRejection(
            @Valid @RequestBody UpdateInspectionStatusRequest request
    ) {
        userCommandService.updateInspectionStatus(request.getBusinessVerificationRequestIds(), InspectionStatus.REJECTED);

        return ApiResponse.ok(null);
    }
}
