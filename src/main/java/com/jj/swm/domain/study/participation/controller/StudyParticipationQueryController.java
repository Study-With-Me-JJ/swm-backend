package com.jj.swm.domain.study.participation.controller;

import com.jj.swm.domain.study.participation.dto.GetStudyParticipationCondition;
import com.jj.swm.domain.study.participation.dto.response.GetStudyParticipationDetailsResponse;
import com.jj.swm.domain.study.participation.dto.response.GetStudyParticipationResponse;
import com.jj.swm.domain.study.participation.service.StudyParticipationQueryService;
import com.jj.swm.global.common.dto.ApiResponse;
import com.jj.swm.global.common.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        PageResponse<GetStudyParticipationResponse> pageResponse =
                participationQueryService.getStudyParticipations(
                        recruitmentPositionId,
                        UUID.fromString(principal.getName()),
                        condition
                );

        return ApiResponse.ok(pageResponse);
    }

    @GetMapping("/v1/recruitment-position/participation/{participationId}")
    @Operation(
            summary = "스터디 참여 상세 조회",
            description = "스터디 참여를 상세 조회합니다. 스터디 작성자, 참여 신청자 전용입니다. "
    )
    public ApiResponse<GetStudyParticipationDetailsResponse> getStudyParticipationDetails(
            @PathVariable("participationId") Long participationId,
            Principal principal
    ) {
        GetStudyParticipationDetailsResponse response = participationQueryService.getStudyParticipationDetails(
                        participationId, UUID.fromString(principal.getName())
                );

        return ApiResponse.ok(response);
    }
}
