package com.jj.swm.domain.study.controller.comment;

import com.jj.swm.domain.study.dto.comment.response.ParentCommentInquiryResponse;
import com.jj.swm.domain.study.dto.comment.response.CommentInquiryResponse;
import com.jj.swm.domain.study.service.comment.CommentQueryService;
import com.jj.swm.global.common.dto.ApiResponse;
import com.jj.swm.global.common.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentQueryController {

    private final CommentQueryService commentQueryService;

    @GetMapping("/v1/study/{studyId}/comment")
    public ApiResponse<PageResponse<ParentCommentInquiryResponse>> getCommentList(
            @PathVariable("studyId") Long studyId, @RequestParam("pageNo") int pageNo
    ) {
        PageResponse<ParentCommentInquiryResponse> pageResponse = commentQueryService.getList(studyId, pageNo);

        return ApiResponse.ok(pageResponse);
    }

    @GetMapping({"/v1/comment/{parentId}/reply", "/v1/comment/{parentId}/reply/{lastReplyId}"})
    public ApiResponse<PageResponse<CommentInquiryResponse>> getReplyList(
            @PathVariable("parentId") Long parentId,
            @PathVariable(value = "lastReplyId", required = false) Long lastReplyId
    ) {
        PageResponse<CommentInquiryResponse> pageResponse = commentQueryService.getReplyList(parentId, lastReplyId);

        return ApiResponse.ok(pageResponse);
    }
}
