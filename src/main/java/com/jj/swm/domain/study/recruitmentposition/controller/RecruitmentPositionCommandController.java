package com.jj.swm.domain.study.recruitmentposition.controller;

import com.jj.swm.domain.study.recruitmentposition.dto.request.CreateRecruitmentPositionRequest;
import com.jj.swm.domain.study.recruitmentposition.dto.request.UpdateRecruitmentPositionRequest;
import com.jj.swm.domain.study.recruitmentposition.dto.response.CreateRecruitmentPositionResponse;
import com.jj.swm.domain.study.recruitmentposition.service.RecruitmentPositionCommandService;
import com.jj.swm.global.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "StudyRecruitmentPosition", description = "<b>[스터디 모집 포지션]</b> API")
public class RecruitmentPositionCommandController {

    private final RecruitmentPositionCommandService recruitmentPositionCommandService;

    @PostMapping("/v1/study/{studyId}/recruitment-position")
    @Operation(
            summary = "스터디 모집 포지션 추가",
            description = "스터디 모집 포지션을 추가합니다. 모집 포지션 개수가 10개를 초과하면 예외가 발생합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201")
    public ApiResponse<CreateRecruitmentPositionResponse> createRecruitmentPosition(
            @Valid @RequestBody CreateRecruitmentPositionRequest request,
            @PathVariable("studyId") Long studyId,
            Principal principal
    ) {
        CreateRecruitmentPositionResponse response = recruitmentPositionCommandService.createRecruitmentPosition(
                request,
                studyId,
                UUID.fromString(principal.getName())
        );

        return ApiResponse.created(response);
    }

    @PatchMapping("/v1/study/recruitment-position/{recruitmentPositionId}")
    @Operation(summary = "스터디 모집 포지션 수정", description = "스터디 모집 포지션을 수정합니다.")
    public ApiResponse<Void> updateRecruitmentPosition(
            @Valid @RequestBody UpdateRecruitmentPositionRequest request,
            @PathVariable("recruitmentPositionId") Long recruitmentPositionId,
            Principal principal
    ) {
        recruitmentPositionCommandService.updateRecruitmentPosition(
                request,
                recruitmentPositionId,
                UUID.fromString(principal.getName())
        );

        return ApiResponse.ok(null);
    }

    @DeleteMapping("/v1/study/recruitment-position/{recruitmentPositionId}")
    @Operation(summary = "스터디 모집 포지션 삭제", description = "스터디 모집 포지션을 삭제합니다.")
    public ApiResponse<Void> deleteRecruitmentPosition(
            @PathVariable("recruitmentPositionId") Long recruitmentPositionId, Principal principal
    ) {
        recruitmentPositionCommandService.deleteRecruitmentPosition(
                recruitmentPositionId, UUID.fromString(principal.getName())
        );

        return ApiResponse.ok(null);
    }
}
