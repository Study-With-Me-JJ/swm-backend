package com.jj.swm.domain.study.controller;

import com.jj.swm.domain.study.dto.request.CommentUpsertRequest;
import com.jj.swm.domain.study.dto.response.CommentCreateResponse;
import com.jj.swm.domain.study.dto.response.CommentUpdateResponse;
import com.jj.swm.domain.study.service.CommentCommandService;
import com.jj.swm.global.common.dto.ApiResponse;
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
            @RequestBody CommentUpsertRequest createRequest
    ) {
        CommentCreateResponse createResponse = commentCommandService.create(
                UUID.fromString("d554b429-366f-4d8e-929d-bb5479623eb9"),
                studyId,
                parentId,
                createRequest
        );

        return ApiResponse.created(createResponse);
    }

    @PutMapping("/v1/study/comment/{commentId}")
    public ApiResponse<CommentUpdateResponse> updateComment(
            Principal principal,
            @PathVariable("commentId") Long commentId,
            @RequestBody CommentUpsertRequest updateRequest
    ) {
        CommentUpdateResponse updateResponse = commentCommandService.update(
                UUID.fromString("d554b429-366f-4d8e-929d-bb5479623eb9"),
                commentId,
                updateRequest
        );

        return ApiResponse.ok(updateResponse);
    }
}
