package com.jj.swm.domain.study.participation.controller;

import com.jj.swm.domain.study.core.dto.response.GetStudyResponse;
import com.jj.swm.domain.study.participation.dto.request.GetStudyParticipationCondition;
import com.jj.swm.domain.study.participation.dto.response.GetStudyParticipationDetailsResponse;
import com.jj.swm.domain.study.participation.dto.response.GetStudyParticipationInMyPageResponse;
import com.jj.swm.domain.study.participation.dto.response.GetStudyParticipationResponse;
import com.jj.swm.domain.study.participation.service.StudyParticipationQueryService;
import com.jj.swm.global.common.dto.ApiResponse;
import com.jj.swm.global.common.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "StudyRecruitmentPosition", description = "<b>[스터디 모집 포지션]</b> API")
public class StudyParticipationQueryController {

    private final StudyParticipationQueryService participationQueryService;

    @GetMapping("/v1/recruitment-position/{recruitmentPositionId}/participation")
    @Operation(
            summary = "스터디 참여 조회",
            description = "스터디 참여 목록을 조회합니다. 스터디 작성자 전용입니다. "
    )
    public ApiResponse<PageResponse<GetStudyParticipationResponse>> getStudyParticipations(
            @PathVariable("recruitmentPositionId") Long recruitmentPositionId,
            Principal principal,
            GetStudyParticipationCondition condition
    ) {
        PageResponse<GetStudyParticipationResponse> pageResponse = participationQueryService.getStudyParticipations(
                recruitmentPositionId,
                UUID.fromString(principal.getName()),
                condition
        );

        return ApiResponse.ok(pageResponse);
    }

    @GetMapping("/v1/study/{studyId}/participation")
    @Operation(
            summary = "스터디 참여 조회",
            description = "마이 페이지에서 스터디 참여 목록을 조회합니다. 스터디 작성자 전용입니다. "
    )
    public ApiResponse<PageResponse<GetStudyParticipationInMyPageResponse>> getStudyParticipationsInMyPage(
            @PathVariable("studyId") Long studyId,
            Principal principal,
            GetStudyParticipationCondition condition
    ) {
        PageResponse<GetStudyParticipationInMyPageResponse> pageResponse =
                participationQueryService.getStudyParticipationsInMyPage(
                        studyId,
                        UUID.fromString(principal.getName()),
                        condition
                );

        return ApiResponse.ok(pageResponse);
    }

    @GetMapping("/v1/study/user/participation")
    @Operation(
            summary = "참여 신청한 스터디 목록 조회",
            description = "참여 신청한 스터디 목록을 조회합니다."
    )
    public ApiResponse<PageResponse<GetStudyResponse>> getUserParticipatedStudies(
            Principal principal, @RequestParam(value = "pageNo", required = false, defaultValue = "0") int pageNo
    ) {
        PageResponse<GetStudyResponse> pageResponse = participationQueryService.getUserParticipatedStudies(
                UUID.fromString(principal.getName()), pageNo
        );

        return ApiResponse.ok(pageResponse);
    }

    @GetMapping("/v1/recruitment-position/participation/{participationId}")
    @Operation(
            summary = "스터디 참여 상세 조회",
            description = "스터디 참여를 상세 조회합니다. 스터디 작성자, 참여 신청자 전용입니다. "
    )
    public ApiResponse<GetStudyParticipationDetailsResponse> getStudyParticipationDetails(
            @PathVariable("participationId") Long participationId, Principal principal
    ) {
        GetStudyParticipationDetailsResponse response = participationQueryService.getStudyParticipationDetails(
                participationId, UUID.fromString(principal.getName())
        );

        return ApiResponse.ok(response);
    }
}
