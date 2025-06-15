package com.jj.swm.domain.study.participation.controller;

import com.jj.swm.domain.study.core.dto.response.GetStudyResponse;
import com.jj.swm.domain.study.participation.dto.request.GetStudyParticipationCondition;
import com.jj.swm.domain.study.participation.dto.response.GetStudyParticipationDetailsResponse;
import com.jj.swm.domain.study.participation.dto.response.GetStudyParticipationInMyPageResponse;
import com.jj.swm.domain.study.participation.dto.response.GetStudyParticipationResponse;
import com.jj.swm.domain.study.participation.service.StudyParticipationQueryService;
import com.jj.swm.global.common.dto.ApiResponse;
import com.jj.swm.global.common.dto.PageResponse;
import com.jj.swm.global.config.SwaggerConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "StudyParticipation", description = "<b>[스터디 참여 신청]</b> API")
public class StudyParticipationQueryController {

    private final StudyParticipationQueryService participationQueryService;

    @GetMapping("/v1/recruitment-position/{recruitmentPositionId}/participation")
    @Operation(
            summary = "스터디 참여 신청 목록 조회",
            description = """
                    PathVariable로 전달된 모집 포지션 ID에 대해 참여 신청 목록을 조회합니다.</br>
                    페이지네이션을 기반으로 응답 데이터가 구성됩니다.</br>
                    한 페이지 내에 참여 신청의 최대 개수는 10개이고, 정렬은 오래된 순만 지원합니다.</br>
                    스터디 작성자만 조회할 수 있습니다. (로그인 필요)
                    """
    )
    @Parameter(
            name = "condition",
            description = """
                    condition은 필수가 아니고 실질적인 요청은 쿼리 파라미터로 status=?&pageNo=?와 같습니다.</br>
                    status와 pageNo 모두 필수가 아닙니다.</br>
                    내부 필드에 대한 자세한 설명은 Schemas의 GetStudyParticipationCondition을 참고하면 됩니다.
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "스터디 작성자 아님",
                    content = @Content(examples = @ExampleObject(value = SwaggerConfig.ERROR_RESPONSE))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "모집 포지션 없음",
                    content = @Content(examples = @ExampleObject(value = SwaggerConfig.ERROR_RESPONSE))
            )
    })
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
            summary = "스터디 참여 신청 목록 조회 in 마이페이지",
            description = """
                    PathVariable로 전달받은 스터디 ID에 대해 마이페이지 내에서 참여 신청 목록을 조회합니다.
                    이때, 모든 모집 포지션에 대한 참여 신청 목록입니다.</br>
                    페이지네이션을 기반으로 응답 데이터가 구성됩니다.</br>
                    한 페이지 내에 참여 신청의 최대 개수는 10개이고, 정렬은 오래된 순만 지원합니다.</br>
                    스터디 작성자만 조회할 수 있습니다. (로그인 필요)
                    """
    )
    @Parameter(
            name = "condition",
            description = """
                    condition은 필수가 아니고 실질적인 요청은 쿼리 파라미터로 status=?&pageNo=?와 같습니다.</br>
                    status와 pageNo 모두 필수가 아닙니다.</br>
                    내부 필드에 대한 자세한 설명은 Schemas의 GetStudyParticipationCondition을 참고하면 됩니다.
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "스터디 작성자 아님",
                    content = @Content(examples = @ExampleObject(value = SwaggerConfig.ERROR_RESPONSE))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "스터디 모집 없음",
                    content = @Content(examples = @ExampleObject(value = SwaggerConfig.ERROR_RESPONSE))
            )
    })
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
            description = """
                    유저가 참여 신청한 스터디 목록을 조회합니다.</br>
                    한 페이지 내에 스터디의 최대 개수는 20개이고, 정렬은 최신순만 지원합니다.</br>
                    페이지네이션을 기반으로 응답 데이터가 구성됩니다.</br>
                    로그인한 유저만 조회할 수 있습니다.
                    """
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
            summary = "스터디 참여 신청 상세 조회",
            description = """
                    PathVariable로 전달된 참여 신청 ID에 대해 스터디 참여 신청을 상세 조회합니다.</br>
                    스터디 작성자, 참여 신청자만 조회할 수 있습니다. (로그인 필요)</br>
                    스터디 작성자의 경우 해당 참여 신청을 승인했을 시에만 kakaoId가 보입니다.
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "스터디 작성자, 참여 신청자 아님",
                    content = @Content(examples = @ExampleObject(value = SwaggerConfig.ERROR_RESPONSE))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "참여 신청 없음",
                    content = @Content(examples = @ExampleObject(value = SwaggerConfig.ERROR_RESPONSE))
            )
    })
    public ApiResponse<GetStudyParticipationDetailsResponse> getStudyParticipationDetails(
            @PathVariable("participationId") Long participationId, Principal principal
    ) {
        GetStudyParticipationDetailsResponse response = participationQueryService.getStudyParticipationDetails(
                participationId, UUID.fromString(principal.getName())
        );

        return ApiResponse.ok(response);
    }
}
