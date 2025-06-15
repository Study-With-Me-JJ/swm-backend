package com.jj.swm.domain.study.core.controller;

import com.jj.swm.domain.study.core.dto.request.GetStudyCondition;
import com.jj.swm.domain.study.core.dto.response.GetStudyDetailsResponse;
import com.jj.swm.domain.study.core.dto.response.GetStudyResponse;
import com.jj.swm.domain.study.core.service.StudyQueryService;
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
@Tag(name = "Study", description = "<b>[스터디 모집]</b> API")
public class StudyQueryController {

    private final StudyQueryService studyQueryService;

    @GetMapping("/v1/study")
    @Operation(
            summary = "스터디 목록 조회",
            description = """
                    스터디 목록을 조회합니다. 이때, 무한 스크롤을 기반으로 응답 데이터가 구성됩니다.<br>
                    한 번 스터디 목록을 조회할 때 스터디의 최대 개수는 20개입니다.</br>
                    로그인한 유저가 조회할 시에 북마크, 좋아요 여부와 참여 신청 상태 정보를 추가적으로 보냅니다.
                    """
    )
    @Parameter(
            name = "condition",
            description = """
                    condition은 필수가 아니고 실질적인 요청은 쿼리 파라미터로 title=?&category=?와 같습니다.</br>
                    모든 내부 필드는 필수가 아닙니다.</br>
                    내부 필드에 대한 자세한 설명은 Schemas의 GetStudyCondition을 참고하면 됩니다.
                    """
    )
    public ApiResponse<PageResponse<GetStudyResponse>> getStudies(Principal principal, GetStudyCondition condition) {
        PageResponse<GetStudyResponse> pageResponse = studyQueryService.getStudies(
                principal != null ? UUID.fromString(principal.getName()) : null, condition
        );

        return ApiResponse.ok(pageResponse);
    }

    @GetMapping("/v1/study/{studyId}")
    @Operation(
            summary = "스터디 상세 조회",
            description = """
                    PathVariable로 전달된 스터디 ID에 대해 상세 조회합니다.</br>
                    로그인한 유저가 조회할 시에 북마크, 좋아요 여부와 참여 신청 상태 정보를 추가적으로 보냅니다.</br>
                    성공적으로 상세 조회시 조회수가 1 증가합니다.
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "스터디 없음",
                    content = @Content(examples = @ExampleObject(value = SwaggerConfig.ERROR_RESPONSE))
            )
    })
    public ApiResponse<GetStudyDetailsResponse> getStudyDetails(
            @PathVariable("studyId") Long studyId, Principal principal
    ) {
        GetStudyDetailsResponse response = studyQueryService.getStudyDetails(
                studyId, principal != null ? UUID.fromString(principal.getName()) : null
        );

        return ApiResponse.ok(response);
    }

    @GetMapping("/v1/study/user/liked-studies")
    @Operation(
            summary = "좋아요한 스터디 목록 조회",
            description = """
                    유저가 좋아요한 스터디 목록을 조회합니다.<br>
                    페이지네이션을 기반으로 응답 데이터가 구성됩니다.</br>
                    한 번 스터디 목록을 조회할 때 스터디의 최대 개수는 20개이고 최신순만 지원합니다.</br>
                    북마크, 좋아요 여부와 참여 신청 상태 정보를 보내지 않습니다.</br>
                    로그인한 유저만 조회할 수 있습니다.
                    """
    )
    public ApiResponse<PageResponse<GetStudyResponse>> getUserLikedStudies(
            Principal principal, @RequestParam(value = "pageNo", required = false, defaultValue = "0") int pageNo
    ) {
        PageResponse<GetStudyResponse> pageResponse = studyQueryService.getUserLikedStudies(
                UUID.fromString(principal.getName()), pageNo
        );

        return ApiResponse.ok(pageResponse);
    }

    @GetMapping("/v1/study/user/bookmarked-studies")
    @Operation(
            summary = "북마크한 스터디 목록 조회",
            description = """
                    유저가 북마크한 스터디 목록을 조회합니다.<br>
                    페이지네이션을 기반으로 응답 데이터가 구성됩니다.</br>
                    한 번 스터디 목록을 조회할 때 스터디의 최대 개수는 20개이고 최신순만 지원합니다.</br>
                    북마크, 좋아요 여부와 참여 신청 상태 정보를 보내지 않습니다.</br>
                    로그인한 유저만 조회할 수 있습니다.
                    """
    )
    public ApiResponse<PageResponse<GetStudyResponse>> getUserBookmarkedStudies(
            Principal principal, @RequestParam(value = "pageNo", required = false, defaultValue = "0") int pageNo
    ) {
        PageResponse<GetStudyResponse> pageResponse = studyQueryService.getUserBookmarkedStudies(
                UUID.fromString(principal.getName()), pageNo
        );

        return ApiResponse.ok(pageResponse);
    }

    @GetMapping("/v1/study/user/studies")
    @Operation(
            summary = "작성한 스터디 목록 조회",
            description = """
                    유저가 작성한 스터디 목록을 조회합니다.<br>
                    페이지네이션을 기반으로 응답 데이터가 구성됩니다.</br>
                    한 번 스터디 목록을 조회할 때 스터디의 최대 개수는 20개이고 최신순만 지원합니다.</br>
                    북마크, 좋아요 여부와 참여 신청 상태 정보를 보내지 않습니다.</br>
                    로그인한 유저만 조회할 수 있습니다.
                    """
    )
    public ApiResponse<PageResponse<GetStudyResponse>> getUserStudies(
            Principal principal, @RequestParam(value = "pageNo", required = false, defaultValue = "0") int pageNo
    ) {
        PageResponse<GetStudyResponse> pageResponse = studyQueryService.getUserStudies(
                UUID.fromString(principal.getName()), pageNo
        );

        return ApiResponse.ok(pageResponse);
    }
}
