package com.jj.swm.domain.study.participation.controller;

import com.jj.swm.domain.study.participation.dto.request.CreateStudyParticipationRequest;
import com.jj.swm.domain.study.participation.dto.request.UpdateStudyParticipationRequest;
import com.jj.swm.domain.study.participation.dto.request.UpdateStudyParticipationStatusRequest;
import com.jj.swm.domain.study.participation.dto.response.UpdateStudyParticipationStatusResponse;
import com.jj.swm.domain.study.participation.service.StudyParticipationCommandService;
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
@Tag(name = "StudyParticipation", description = "<b>[스터디 참여]</b> API")
public class StudyParticipationCommandController {

    private final StudyParticipationCommandService participationCommandService;

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
        participationCommandService.createStudyParticipation(
                request,
                recruitmentPositionId,
                UUID.fromString(principal.getName())
        );

        return ApiResponse.created(null);
    }

    @PatchMapping("/v1/recruitment-position/participation/{participationId}/status")
    @Operation(
            summary = "스터디 참여 상태 수정",
            description = "스터디 참여 상태를 수정합니다. 승인 시에만 kakaoId를 전송해줍니다."
    )
    public ApiResponse<UpdateStudyParticipationStatusResponse> updateStudyParticipationStatus(
            @Valid @RequestBody UpdateStudyParticipationStatusRequest request,
            @PathVariable("participationId") Long participationId,
            Principal principal
    ) {
        UpdateStudyParticipationStatusResponse response =
                participationCommandService.updateStudyParticipationStatus(
                        request,
                        participationId,
                        UUID.fromString(principal.getName())
                );

        return ApiResponse.ok(response);
    }

    @PatchMapping("/v1/recruitment-position/{recruitmentPositionId}/participation/{participationId}/position")
    @Operation(summary = "스터디 참여의 모집 포지션 수정", description = "스터디 참여의 모집 포지션을 수정합니다.")
    public ApiResponse<Void> updateStudyParticipationPosition(
            @PathVariable("recruitmentPositionId") Long recruitmentPositionId,
            @PathVariable("participationId") Long participationId,
            Principal principal
    ) {
        participationCommandService.updateStudyParticipationPosition(
                recruitmentPositionId,
                participationId,
                UUID.fromString(principal.getName())
        );

        return ApiResponse.ok(null);
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
        participationCommandService.updateStudyParticipation(
                request,
                participationId,
                UUID.fromString(principal.getName())
        );

        return ApiResponse.ok(null);
    }

    @DeleteMapping("/v1/recruitment-position/participation/{participationId}")
    @Operation(
            summary = "스터디 참여 삭제",
            description = """
                    스터디 참여를 삭제합니다.
                    승인된 참여 신청은 삭제가 불가능합니다.
                    거절된 참여 신청을 삭제하면 3일 뒤에 재생성할 수 있습니다.
                    """
    )
    public ApiResponse<Void> deleteStudyParticipation(
            @PathVariable("participationId") Long participationId, Principal principal
    ) {
        participationCommandService.deleteStudyParticipation(
                participationId, UUID.fromString(principal.getName())
        );

        return ApiResponse.ok(null);
    }
}
