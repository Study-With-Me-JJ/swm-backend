package com.jj.swm.domain.study.comment.controller;

import com.jj.swm.domain.study.comment.dto.request.UpsertStudyCommentRequest;
import com.jj.swm.domain.study.comment.dto.response.CreateStudyCommentResponse;
import com.jj.swm.domain.study.comment.dto.response.UpdateStudyCommentResponse;
import com.jj.swm.domain.study.comment.service.StudyCommentCommandService;
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
@Tag(name = "StudyComment", description = "<b>[스터디 댓글]</b> API")
public class StudyCommentCommandController {

    private final StudyCommentCommandService commentCommandService;

    @PostMapping({"/v1/study/{studyId}/comment", "/v1/study/{studyId}/comment/{parentId}"})
    @Operation(
            summary = "스터디 댓글 생성",
            description = "스터디 댓글을 생성합니다. 대댓글도 같은 API를 사용합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201")
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
    @Operation(summary = "스터디 댓글 수정", description = "스터디 댓글을 수정합니다.")
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
    @Operation(summary = "스터디 댓글 삭제", description = "스터디 댓글을 삭제합니다.")
    public ApiResponse<Void> deleteComment(
            @PathVariable("commentId") Long commentId,
            Principal principal
    ) {
        commentCommandService.deleteComment(commentId, UUID.fromString(principal.getName()));

        return ApiResponse.ok(null);
    }
}
