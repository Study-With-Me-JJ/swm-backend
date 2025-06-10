package com.jj.swm.domain.study.comment.controller;

import com.jj.swm.domain.study.comment.dto.request.UpsertStudyCommentRequest;
import com.jj.swm.domain.study.comment.dto.response.CreateStudyCommentResponse;
import com.jj.swm.domain.study.comment.dto.response.UpdateStudyCommentResponse;
import com.jj.swm.domain.study.comment.service.StudyCommentCommandService;
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
@Tag(name = "StudyComment", description = "<b>[스터디 댓글]</b> API")
public class StudyCommentCommandController {

    private final StudyCommentCommandService commentCommandService;

    @PostMapping({"/v1/study/{studyId}/comment", "/v1/study/{studyId}/comment/{parentId}"})
    @Operation(
            summary = "스터디 댓글/대댓글 생성",
            description = """
                    앞쪽의 PathVariable로 전달된 스터디 ID를 기반으로 해당 스터디에 댓글/대댓글을 생성합니다.</br>
                    뒤쪽의 PathVariable로 전달된 부모 댓글 ID를 기반으로 대댓글을 생성합니다.</br>
                    로그인한 유저만 댓글/대댓글을 생성할 수 있습니다.</br>
                    생성 성공 시에 스터디의 댓글 수가 1 증가합니다.
                    """

    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "생성 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = "스터디 or 부모 댓글 없음"
            )
    })
    public ApiResponse<CreateStudyCommentResponse> createComment(
            @Valid @RequestBody UpsertStudyCommentRequest createRequest,
            @PathVariable("studyId") Long studyId,
            @PathVariable(value = "parentId", required = false) Long parentId,
            Principal principal
    ) {
        CreateStudyCommentResponse response = commentCommandService.createComment(
                createRequest,
                studyId,
                parentId,
                UUID.fromString(principal.getName())
        );

        return ApiResponse.created(response);
    }

    @PatchMapping("/v1/study/comment/{commentId}")
    @Operation(
            summary = "스터디 댓글 수정",
            description = """
                    PathVariable로 전달된 댓글 ID를 기반으로 스터디 댓글을 수정합니다.</br>
                    해당 댓글을 작성한 유저만 수정할 수 있습니다. (로그인 필요)
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "댓글 없음")
    })
    public ApiResponse<UpdateStudyCommentResponse> updateComment(
            @Valid @RequestBody UpsertStudyCommentRequest updateRequest,
            @PathVariable("commentId") Long commentId,
            Principal principal
    ) {
        UpdateStudyCommentResponse response = commentCommandService.updateComment(
                updateRequest,
                commentId,
                UUID.fromString(principal.getName())
        );

        return ApiResponse.ok(response);
    }

    @DeleteMapping("/v1/study/comment/{commentId}")
    @Operation(
            summary = "스터디 댓글 삭제",
            description = """
                    PathVariable로 전달된 댓글 ID를 기반으로 스터디 댓글을 삭제합니다.</br>
                    부모 댓글일 시에 자식 댓글까지 모두 삭제됩니다.</br>
                    해당 댓글을 작성한 유저만 삭제할 수 있습니다. (로그인 필요)</br>
                    삭제 성공 시에 스터디의 댓글 수가 1 감소합니다.
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "댓글 없음")
    })
    public ApiResponse<Void> deleteComment(
            @PathVariable("commentId") Long commentId,
            Principal principal
    ) {
        commentCommandService.deleteComment(commentId, UUID.fromString(principal.getName()));

        return ApiResponse.ok(null);
    }
}
