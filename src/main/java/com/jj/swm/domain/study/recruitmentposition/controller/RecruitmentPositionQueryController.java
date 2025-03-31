package com.jj.swm.domain.study.recruitmentposition.controller;

import com.jj.swm.domain.study.recruitmentposition.dto.GetStudyParticipationCondition;
import com.jj.swm.domain.study.recruitmentposition.dto.response.GetStudyParticipationResponse;
import com.jj.swm.domain.study.recruitmentposition.service.RecruitmentPositionQueryService;
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
public class RecruitmentPositionQueryController {

    private final RecruitmentPositionQueryService recruitmentPositionQueryService;

    @GetMapping("/v1/study/{studyId}/recruitment-position/{recruitmentPositionId}")
    @Operation(
            summary = "스터디 참여 조회",
            description = "스터디 참여 목록을 조회합니다. 스터디 작성자 전용입니다. "
    )
    public ApiResponse<PageResponse<GetStudyParticipationResponse>> getStudyParticipations(
            @PathVariable("studyId") Long studyId,
            @PathVariable("recruitmentPositionId") Long recruitmentPositionId,
            Principal principal,
            GetStudyParticipationCondition condition
    ) {
        PageResponse<GetStudyParticipationResponse> pageResponse =
                recruitmentPositionQueryService.getStudyParticipations(
                        studyId,
                        recruitmentPositionId,
                        UUID.fromString(principal.getName()),
                        condition
                );

        return ApiResponse.ok(pageResponse);
    }
}
