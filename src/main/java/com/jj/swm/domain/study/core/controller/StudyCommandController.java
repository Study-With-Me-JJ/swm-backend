package com.jj.swm.domain.study.core.controller;

import com.jj.swm.domain.study.core.dto.request.*;
import com.jj.swm.domain.study.core.dto.response.CreateRecruitmentPositionResponse;
import com.jj.swm.domain.study.core.dto.response.CreateStudyBookmarkResponse;
import com.jj.swm.domain.study.core.dto.response.CreateStudyLikeResponse;
import com.jj.swm.domain.study.core.service.StudyCommandService;
import com.jj.swm.global.common.dto.ApiResponse;
import com.jj.swm.global.config.SwaggerConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "Study", description = "<b>[스터디 모집]</b> API")
public class StudyCommandController {

    private final StudyCommandService studyCommandService;

    @PostMapping("/v1/study")
    @Operation(
            summary = "스터디 모집 생성",
            description = """
                    스터디 모집을 생성합니다.</br>
                    생성 성공 시에 ID 값을 제공하지 않으므로 새로고침을 통해 조회해야 합니다.</br>
                    모집 포지션의 경우 최소 1개는 존재해야 합니다.</br>
                    로그인한 유저만 스터디 모집을 생성할 수 있습니다.
                    """
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201")
    public ApiResponse<Void> createStudy(@Valid @RequestBody CreateStudyRequest request, Principal principal) {
        studyCommandService.createStudy(request, UUID.fromString(principal.getName()));

        return ApiResponse.created(null);
    }

    @PatchMapping("/v1/study/{studyId}")
    @Operation(
            summary = "스터디 모집 수정",
            description = """
                    PathVariable로 전달된 스터디 ID에 대해 수정합니다.</br>
                    태그, 이미지의 개수가 개수가 10개를 초과하면 안됩니다.</br>
                    수정 성공 시에 태그와 이미지의 ID 값을 제공하지 않으므로 새로고침을 통해 조회해야 합니다.</br>
                    스터디 모집 작성자만 수정할 수 있습니다. (로그인 필요)
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = " 태그 허용 개수 초과</br>" +
                            "이미지 허용 개수 초과",
                    content = @Content(examples = @ExampleObject(value = SwaggerConfig.ERROR_RESPONSE))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = """
                            스터디 없음</br>
                            태그 없음</br>
                            이미지 없음</br>
                            """,
                    content = @Content(examples = @ExampleObject(value = SwaggerConfig.ERROR_RESPONSE))
            )
    })
    public ApiResponse<Void> updateStudy(
            @Valid @RequestBody UpdateStudyRequest request,
            @PathVariable("studyId") Long studyId,
            Principal principal
    ) {
        studyCommandService.updateStudy(
                request,
                studyId,
                UUID.fromString(principal.getName())
        );

        return ApiResponse.ok(null);
    }

    @PatchMapping("/v1/study/{studyId}/status")
    @Operation(
            summary = "스터디 상태 수정",
            description = """
                    PathVariable로 전달된 스터디 ID에 대해 상태값을 수정합니다.</br>
                    스터디 모집의 상태값은 모집중인지에 대한 지표입니다.</br>
                    스터디 모집 작성자만 수정할 수 있습니다. (로그인 필요)
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "스터디 없음",
                    content = @Content(examples = @ExampleObject(value = SwaggerConfig.ERROR_RESPONSE))
            )
    })
    public ApiResponse<Void> updateStudyStatus(
            @Valid @RequestBody UpdateStudyStatusRequest request,
            @PathVariable("studyId") Long studyId,
            Principal principal
    ) {
        studyCommandService.updateStudyStatus(
                request,
                studyId,
                UUID.fromString(principal.getName())
        );

        return ApiResponse.ok(null);
    }

    @DeleteMapping("/v1/study")
    @Operation(
            summary = "스터디 다중 삭제",
            description = """
                    스터디 모집을 다중 삭제합니다. 단일 삭제로도 사용됩니다.</br>
                    삭제할 수 있는 최대 개수는 한 페이지 내의 스터디 모집의 개수와 같습니다. (20개)</br>
                    스터디 작성자만 삭제할 수 있습니다.
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "스터디 없음",
                    content = @Content(examples = @ExampleObject(value = SwaggerConfig.ERROR_RESPONSE))
            )
    })
    public ApiResponse<Void> deleteStudies(@Valid @RequestBody DeleteStudyRequest request, Principal principal) {
        studyCommandService.deleteStudies(request, UUID.fromString(principal.getName()));

        return ApiResponse.ok(null);
    }

    @PostMapping("/v1/study/{studyId}/bookmark")
    @Operation(
            summary = "스터디 북마크 생성",
            description = """
                    PathVariable로 전달된 스터디 ID에 대해 북마크를 생성합니다.</br>
                    스터디마다 유저는 북마크를 한 번만 생성할 수 있습니다.</br>
                    로그인한 유저만 북마크를 생성할 수 있습니다.
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "생성 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "이미 북마크 존재",
                    content = @Content(examples = @ExampleObject(value = SwaggerConfig.ERROR_RESPONSE))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "스터디 없음",
                    content = @Content(examples = @ExampleObject(value = SwaggerConfig.ERROR_RESPONSE))
            )
    })
    public ApiResponse<CreateStudyBookmarkResponse> createStudyBookmark(
            @PathVariable("studyId") Long studyId, Principal principal
    ) {
        CreateStudyBookmarkResponse response = studyCommandService.createStudyBookmark(
                studyId, UUID.fromString(principal.getName())
        );

        return ApiResponse.created(response);
    }

    @DeleteMapping("/v1/study/bookmark/{bookmarkId}")
    @Operation(
            summary = "스터디 북마크 삭제",
            description = "PathVariable로 전달된 북마크 ID에 대해 삭제합니다.</br>" +
                    "북마크를 생성한 당사자만 삭제할 수 있습니다. (로그인 필요)"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "북마크 없음",
                    content = @Content(examples = @ExampleObject(value = SwaggerConfig.ERROR_RESPONSE))
            )
    })
    public ApiResponse<Void> deleteStudyBookmark(@PathVariable("bookmarkId") Long bookmarkId, Principal principal) {
        studyCommandService.deleteStudyBookmark(bookmarkId, UUID.fromString(principal.getName()));

        return ApiResponse.ok(null);
    }

    @PostMapping("/v1/study/{studyId}/like")
    @Operation(
            summary = "스터디 좋아요 생성",
            description = """
                    PathVariable로 전달된 스터디 ID에 대해 좋아요를 생성합니다.</br>
                    스터디마다 유저는 좋아요를 한 번만 생성할 수 있습니다.</br>
                    로그인한 유저만 좋아요를 생성할 수 있습니다.</br>
                    생성 성공 시에 스터디의 좋아요 수가 1 증가합니다.
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "생성 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "이미 좋아요 존재",
                    content = @Content(examples = @ExampleObject(value = SwaggerConfig.ERROR_RESPONSE))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "스터디 없음",
                    content = @Content(examples = @ExampleObject(value = SwaggerConfig.ERROR_RESPONSE))
            )
    })
    public ApiResponse<CreateStudyLikeResponse> createStudyLike(
            @PathVariable("studyId") Long studyId, Principal principal
    ) {
        CreateStudyLikeResponse response = studyCommandService.createStudyLike(
                studyId, UUID.fromString(principal.getName())
        );

        return ApiResponse.created(response);
    }

    @DeleteMapping("/v1/study/like/{likeId}")
    @Operation(
            summary = "스터디 좋아요 삭제",
            description = """
                    PathVariable로 전달된 좋아요 ID에 대해 삭제합니다.</br>
                    좋아요를 생성한 당사자만 삭제할 수 있습니다. (로그인 필요)</br>
                    삭제 성공 시에 스터디의 좋아요 수가 1 감소합니다.
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "좋아요 없음",
                    content = @Content(examples = @ExampleObject(value = SwaggerConfig.ERROR_RESPONSE))
            )
    })
    public ApiResponse<Void> deleteStudyLike(@PathVariable("likeId") Long likeId, Principal principal) {
        studyCommandService.deleteStudyLike(likeId, UUID.fromString(principal.getName()));

        return ApiResponse.ok(null);
    }

    @PatchMapping("/v1/study/{studyId}/recruitment-position")
    @Operation(
            summary = "스터디 모집 포지션 변경",
            description = """
                    PathVariable로 전달된 스터디 ID에 대해 모집 포지션을 변경합니다.</br>
                    태그, 이미지를 수정할 때와 달리 추가, 삭제 뿐만 아니라 직접 수정도 할 수 있습니다.</br>
                    모집 포지션의 개수가 개수가 1개 미만이거나 10개를 초과하면 안됩니다.</br>
                    새로 생성된 모집 포지션에 대해서만 응답 데이터를 보내줍니다.
                    이때, 참여 신청 수는 0으로 설정하면 됩니다.</br>
                    스터디 작성자만 모집 포지션을 변경할 수 있습니다.
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "변경 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = """
                            모집포지션 허용 개수 미만 및 초과</br>
                            수정할 모집 포지션과 삭제할 모집 포지션 중복</br>
                            승인 인원이 모집 인원보다 큰 값
                            """,
            content = @Content(examples = @ExampleObject(value = SwaggerConfig.ERROR_RESPONSE))
            ),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "스터디 없음</br>" +
                    "모집 포지션 없음",
            content = @Content(examples = @ExampleObject(value = SwaggerConfig.ERROR_RESPONSE))
    )
})

public ApiResponse<List<CreateRecruitmentPositionResponse>> modifyRecruitmentPosition(
        @Valid @RequestBody ModifyRecruitmentPositionRequest request,
        @PathVariable("studyId") Long studyId,
        Principal principal
) {
    List<CreateRecruitmentPositionResponse> responses = studyCommandService.modifyRecruitmentPosition(
            request,
            studyId,
            UUID.fromString(principal.getName())
    );

    return ApiResponse.ok(responses);
}
}
