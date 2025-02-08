package com.jj.swm.domain.study.controller.comment;

import com.jj.swm.domain.study.dto.comment.request.CommentUpsertRequest;
import com.jj.swm.domain.study.dto.comment.response.CommentCreateResponse;
import com.jj.swm.domain.study.dto.comment.response.CommentUpdateResponse;
import com.jj.swm.domain.study.service.comment.CommentCommandService;
import com.jj.swm.global.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentCommandController {

    private final CommentCommandService commentCommandService;

    @PostMapping({"/v1/study/{studyId}/comment", "/v1/study/{studyId}/comment/{parentId}"})
    public ApiResponse<CommentCreateResponse> createComment(
            Principal principal,
            @PathVariable("studyId") Long studyId,
            @PathVariable(value = "parentId", required = false) Long parentId,
            @Valid @RequestBody CommentUpsertRequest createRequest
    ) {
        CommentCreateResponse createResponse = commentCommandService.create(
                UUID.fromString(principal.getName()),
                studyId,
                parentId,
                createRequest
        );

        return ApiResponse.created(createResponse);
    }

    @PatchMapping("/v1/study/comment/{commentId}")
    public ApiResponse<CommentUpdateResponse> updateComment(
            Principal principal,
            @PathVariable("commentId") Long commentId,
            @Valid @RequestBody CommentUpsertRequest updateRequest
    ) {
        CommentUpdateResponse updateResponse = commentCommandService.update(
                UUID.fromString(principal.getName()),
                commentId,
                updateRequest
        );

        return ApiResponse.ok(updateResponse);
    }

    @DeleteMapping("/v1/study/{studyId}/comment/{commentId}")
    public ApiResponse<Void> deleteComment(
            Principal principal,
            @PathVariable("studyId") Long studyId,
            @PathVariable("commentId") Long commentId
    ) {
        commentCommandService.delete(
                UUID.fromString(principal.getName()),
                studyId,
                commentId
        );

        return ApiResponse.ok(null);
    }
}
