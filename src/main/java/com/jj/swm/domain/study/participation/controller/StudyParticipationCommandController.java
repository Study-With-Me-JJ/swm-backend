
package com.jj.swm.domain.study.participation.controller;

import com.jj.swm.domain.study.participation.dto.request.CreateStudyParticipationRequest;
import com.jj.swm.domain.study.participation.dto.request.UpdateStudyParticipationRequest;
import com.jj.swm.domain.study.participation.dto.request.UpdateStudyParticipationStatusRequest;
import com.jj.swm.domain.study.participation.dto.response.UpdateStudyParticipationStatusResponse;
import com.jj.swm.domain.study.participation.service.StudyParticipationCommandService;
import com.jj.swm.global.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "StudyParticipation", description = "<b>[스터디 참여 신청]</b> API")
public class StudyParticipationCommandController {

    private final StudyParticipationCommandService participationCommandService;

    @PostMapping("/v1/recruitment-position/{recruitmentPositionId}/participation")
    @Operation(
            summary = "스터디 참여 신청 생성",
            description = """
                    PathVariable로 전달된 모집 포지션 ID에 대한 참여 신청을 생성합니다.</br>
                    생성 성공 시에 ID 값을 제공하지 않으므로 새로고침을 통해 조회해야 합니다.</br>
                    스터디 모집 글마다 모집 포지션 상관없이 유저당 한 번만 참여 신청할 수 있습니다.</br>
                    거절 상태의 참여 신청을 지우면, 3일 뒤에 다시 참여 신청을 생성할 수 있습니다.
                    대기 상태에서 참여 신청을 지우는 경우와는 무관합니다.</br>
                    모집 포지션의 모집 인원과 승인 인원이 같으면 참여 신청을 할 수 없습니다.</br>
                    로그인한 유저만 참여 신청을 생성할 수 있습니다.
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "생성 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = """
                            이미 존재하는 참여 신청</br>
                            거절 상태의 참여 신청을 지우고 3일이 지나기 전</br>
                            모집 포지션의 모집 인원과 승인 인원이 동일
                            """
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "모집 포지션 없음")
    })
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
            summary = "스터디 참여 신청 상태 수정",
            description = """
                    PathVariable로 전달된 참여 신청 ID에 대해 상태값을 수정합니다.</br>
                    성공적으로 승인 상태로 변경 시에만 kakaoId를 전송해줍니다.
                    즉, 거절 상태로의 변경에서는 kakaoId를 전송해주지 않습니다.</br>
                    스터디 작성자만 수정할 수 있습니다. (로그인 필요)
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = " 참여 신청의 상태가 이미 대기 상태가 아님</br>" +
                            "모집 포지션의 모집 인원과 승인 인원이 동일"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "스터디 작성자 아님"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "참여 신청 없음")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "status - ACCEPTED, REJECTED만 허용")
    public ApiResponse<UpdateStudyParticipationStatusResponse> updateStudyParticipationStatus(
            @Valid @RequestBody UpdateStudyParticipationStatusRequest request,
            @PathVariable("participationId") Long participationId,
            Principal principal
    ) {
        UpdateStudyParticipationStatusResponse response = participationCommandService.updateStudyParticipationStatus(
                request,
                participationId,
                UUID.fromString(principal.getName())
        );

        return ApiResponse.ok(response);
    }

    @PatchMapping("/v1/recruitment-position/participation/{participationId}")
    @Operation(
            summary = "스터디 참여 신청 수정",
            description = """
                    PathVariable로 전달된 참여 신청 ID에 대해 수정합니다.</br>
                    수정 성공 시에 첨부 링크의 ID 값을 제공하지 않으므로 새로고침을 통해 조회해야 합니다.</br>
                    승인된 참여 신청은 수정이 불가능합니다.</br>
                    첨부 링크의 개수가 3개를 초과하면 안 됩니다.</br>
                    참여 신청자만 수정할 수 있습니다. (로그인 필요)
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "이미 승인된 참여 신청</br>" +
                            "첨부 링크 허용 개수 초과"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "참여 신청 없음</br>" +
                            "첨부 링크 없음"
            )
    })
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

    @PatchMapping("/v1/recruitment-position/{recruitmentPositionId}/participation/{participationId}/position")
    @Operation(
            summary = "스터디 참여 신청의 모집 포지션 수정",
            description = """
                    뒤쪽의 PathVariable로 전달된 참여 신청 ID에 대해
                    앞쪽의 PathVariable로 전달된 모집 포지션 ID로 수정합니다.</br>
                    승인된 참여 신청은 수정이 불가능합니다.</br>
                    수정할 모집 포지션의 모집 인원과 승인 인원이 같으면 수정할 수 없습니다.</br>
                    참여 신청자만 수정할 수 있습니다. (로그인 필요)
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = """
                            이미 승인된 참여 신청</br>
                            기존 모집 포지션과 새로운 모집 포지션이 속한 스터디 모집 글이 같지 않음</br>
                            모집 포지션의 모집 인원과 승인 인원이 동일
                            """
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "참여 신청 없음</br>" +
                            "모집 포지션 없음"
            )
    })
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

    @DeleteMapping("/v1/recruitment-position/participation/{participationId}")
    @Operation(
            summary = "스터디 참여 신청 삭제",
            description = """
                    PathVariable로 전달된 참여 신청 ID에 대해 삭제합니다.</br>
                    승인된 참여 신청은 삭제가 불가능합니다.</br>
                    참여 신청자만 삭제할 수 있습니다. (로그인 필요)
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", description = "이미 승인된 참여 신청"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "참여 신청 없음")
    })
    public ApiResponse<Void> deleteStudyParticipation(
            @PathVariable("participationId") Long participationId, Principal principal
    ) {
        participationCommandService.deleteStudyParticipation(participationId, UUID.fromString(principal.getName()));

        return ApiResponse.ok(null);
    }
}
