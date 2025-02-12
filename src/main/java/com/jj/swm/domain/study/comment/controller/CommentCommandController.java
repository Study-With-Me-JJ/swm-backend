package com.jj.swm.domain.study.comment.controller;

import com.jj.swm.domain.study.comment.dto.request.UpsertCommentRequest;
import com.jj.swm.domain.study.comment.dto.response.AddCommentResponse;
import com.jj.swm.domain.study.comment.dto.response.ModifyCommentResponse;
import com.jj.swm.domain.study.comment.service.CommentCommandService;
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
    public ApiResponse<AddCommentResponse> commentAdd(
            Principal principal,
            @PathVariable("studyId") Long studyId,
            @PathVariable(value = "parentId", required = false) Long parentId,
            @Valid @RequestBody UpsertCommentRequest createRequest
    ) {
        AddCommentResponse response = commentCommandService.addComment(
                UUID.fromString(principal.getName()),
                studyId,
                parentId,
                createRequest
        );

        return ApiResponse.created(response);
    }

    @PatchMapping("/v1/study/comment/{commentId}")
    public ApiResponse<ModifyCommentResponse> commentModify(
            Principal principal,
            @PathVariable("commentId") Long commentId,
            @Valid @RequestBody UpsertCommentRequest modifyRequest
    ) {
        ModifyCommentResponse response = commentCommandService.modifyComment(
                UUID.fromString(principal.getName()),
                commentId,
                modifyRequest
        );

        return ApiResponse.ok(response);
    }

    @DeleteMapping("/v1/study/{studyId}/comment/{commentId}")
    public ApiResponse<Void> commentRemove(
            Principal principal,
            @PathVariable("studyId") Long studyId,
            @PathVariable("commentId") Long commentId
    ) {
        commentCommandService.removeComment(
                UUID.fromString(principal.getName()),
                studyId,
                commentId
        );

        return ApiResponse.ok(null);
    }
}
