package com.jj.swm.domain.study.recruitmentposition.controller;

import com.jj.swm.domain.study.recruitmentposition.dto.request.CreateStudyParticipationRequest;
import com.jj.swm.domain.study.recruitmentposition.dto.request.UpdateStudyParticipationRequest;
import com.jj.swm.domain.study.recruitmentposition.dto.request.UpdateStudyParticipationStatusRequest;
import com.jj.swm.domain.study.recruitmentposition.dto.request.UpsertRecruitmentPositionRequest;
import com.jj.swm.domain.study.recruitmentposition.dto.response.CreateRecruitmentPositionResponse;
import com.jj.swm.domain.study.recruitmentposition.dto.response.UpdateStudyParticipationStatusResponse;
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
            description = """
                    스터디 모집 포지션을 추가합니다.
                    모집 포지션 개수가 10개를 초과하면 예외가 발생합니다.
                    생성 후 바로 화면에 띄울 시 acceptedCount는 바로 0으로 설정해주시면 됩니다.
                    """
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201")
    public ApiResponse<CreateRecruitmentPositionResponse> createRecruitmentPosition(
            @Valid @RequestBody UpsertRecruitmentPositionRequest request,
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
            @Valid @RequestBody UpsertRecruitmentPositionRequest request,
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

    @PostMapping("/v1/recruitment-position/{recruitmentPositionId}/participation")
    @Operation(
            summary = "스터디 참여 생성",
            description = "스터디 참여를 생성합니다. 생성된 것에 대해 id값을 안 주므로 새로고침을 해서 조회해야 합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201")
    public ApiResponse<Void> createStudyParticipation(
            @Valid @RequestBody CreateStudyParticipationRequest request,
            @PathVariable("recruitmentPositionId") Long recruitmentPositionId,
            Principal principal
    ) {
        recruitmentPositionCommandService.createStudyParticipation(
                request,
                recruitmentPositionId,
                UUID.fromString(principal.getName())
        );

        return ApiResponse.created(null);
    }

    @PatchMapping("/v1/recruitment-position/participation/{participationId}/status")
    @Operation(
            summary = "스터디 참여 상태 수정",
            description = "스터디 참여 상태를 수정합니다. 승인 시 kakaoId를 전송해줍니다."
    )
    public ApiResponse<UpdateStudyParticipationStatusResponse> updateStudyParticipationStatus(
            @Valid @RequestBody UpdateStudyParticipationStatusRequest request,
            @PathVariable("participationId") Long participationId,
            Principal principal
    ) {
        UpdateStudyParticipationStatusResponse response =
                recruitmentPositionCommandService.updateStudyParticipationStatus(
                        request,
                        participationId,
                        UUID.fromString(principal.getName())
                );

        return ApiResponse.ok(response);
    }

    @PatchMapping("/v1/recruitment-position/participation/{participationId}")
    @Operation(
            summary = "스터디 참여 수정",
            description = """
                    스터디 참여를 수정합니다. 새 링크에 대해 id값을 안 주므로 새로고침을 해야 합니다.
                    승인된 참여 신청은 수정이 불가능합니다.
                    """
    )
    public ApiResponse<Void> updateStudyParticipation(
            @Valid @RequestBody UpdateStudyParticipationRequest request,
            @PathVariable("participationId") Long participationId,
            Principal principal
    ) {
        recruitmentPositionCommandService.updateStudyParticipation(
                request,
                participationId,
                UUID.fromString(principal.getName())
        );

        return ApiResponse.ok(null);
    }

    @DeleteMapping("/v1/recruitment-position/participation/{participationId}")
    @Operation(
            summary = "스터디 참여 삭제",
            description = "스터디 참여를 삭제합니다. 승인된 참여 신청은 삭제가 불가능합니다."
    )
    public ApiResponse<Void> deleteStudyParticipation(
            @PathVariable("participationId") Long participationId, Principal principal
    ) {
        recruitmentPositionCommandService.deleteStudyParticipation(
                participationId, UUID.fromString(principal.getName())
        );

        return ApiResponse.ok(null);
    }
}
