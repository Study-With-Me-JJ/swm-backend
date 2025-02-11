package com.jj.swm.domain.study.controller.recruitmentposition;

import com.jj.swm.domain.study.dto.recruitmentposition.request.RecruitPositionCreateRequest;
import com.jj.swm.domain.study.dto.recruitmentposition.request.RecruitPositionUpdateRequest;
import com.jj.swm.domain.study.dto.recruitmentposition.response.RecruitmentPositionCreateResponse;
import com.jj.swm.domain.study.service.recruitmentposition.RecruitmentPositionCommandService;
import com.jj.swm.global.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class RecruitmentPositionCommandController {

    private final RecruitmentPositionCommandService recruitmentPositionCommandService;

    @PostMapping("/v1/study/{studyId}/recruitment-position")
    public ApiResponse<RecruitmentPositionCreateResponse> createRecruitmentPosition(
            Principal principal,
            @PathVariable("studyId") Long studyId,
            @Valid @RequestBody RecruitPositionCreateRequest createRequest
    ) {
        RecruitmentPositionCreateResponse createResponse = recruitmentPositionCommandService.create(
                UUID.fromString(principal.getName()),
                studyId,
                createRequest
        );

        return ApiResponse.created(createResponse);
    }

    @PatchMapping("/v1/study/recruitment-position/{recruitPositionId}")
    public ApiResponse<Void> updateRecruitmentPosition(
            Principal principal,
            @PathVariable("recruitPositionId") Long recruitPositionId,
            @Valid @RequestBody RecruitPositionUpdateRequest updateRequest
    ) {
        recruitmentPositionCommandService.update(
                UUID.fromString(principal.getName()),
                recruitPositionId,
                updateRequest
        );

        return ApiResponse.ok(null);
    }

    @DeleteMapping("/v1/study/recruitment-position/{recruitPositionId}")
    public ApiResponse<Void> deleteRecruitmentPosition(
            Principal principal, @PathVariable("recruitPositionId") Long recruitPositionId
    ) {
        recruitmentPositionCommandService.delete(UUID.fromString(principal.getName()), recruitPositionId);

        return ApiResponse.ok(null);
    }

//    TODO 폼 형식일 때 재구현
//    @PostMapping("/v1/study/recruitment-position/{recruitPositionId}")
//    public ApiResponse<RecruitmentPositionApplyResponse> applyRecruitmentPosition(
//            Principal principal, @PathVariable("recruitPositionId") Long recruitPositionId
//    ) {
//        RecruitmentPositionApplyResponse applyResponse = recruitmentPositionCommandService.apply(
//                UUID.fromString("d554b429-366f-4d8e-929d-bb5479623eb9"), recruitPositionId
//        );
//
//        return ApiResponse.created(applyResponse);

//    }

//    TODO 폼 형식일 때 재 구현
//    @DeleteMapping("/v1/study/participant/{participantId}")
//    public ApiResponse<Void> cancelRecruitmentApplication(
//            Principal principal, @PathVariable("participantId") Long participantId
//    ) {
//        recruitmentPositionCommandService.withdraw(
//                UUID.fromString("d554b429-366f-4d8e-929d-bb5479623eb9"), participantId
//        );
//
//        return ApiResponse.ok(null);
//    }
}
