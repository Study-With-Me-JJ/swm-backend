package com.jj.swm.domain.study.controller;

import com.jj.swm.domain.study.dto.response.CommentInquiryResponse;
import com.jj.swm.domain.study.dto.response.ReplyInquiryResponse;
import com.jj.swm.domain.study.service.CommentQueryService;
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
    public ApiResponse<PageResponse<CommentInquiryResponse>> getCommentList(
            @PathVariable("studyId") Long studyId, @RequestParam("pageNo") int pageNo
    ) {
        PageResponse<CommentInquiryResponse> pageResponse = commentQueryService.getList(studyId, pageNo);

        return ApiResponse.ok(pageResponse);
    }

    @GetMapping({"/v1/comment/{parentId}/reply", "/v1/comment/{parentId}/reply/{lastReplyId}"})
    public ApiResponse<PageResponse<ReplyInquiryResponse>> getReplyList(
            @PathVariable("parentId") Long parentId,
            @PathVariable(value = "lastReplyId", required = false) Long lastReplyId
    ) {
        PageResponse<ReplyInquiryResponse> pageResponse = commentQueryService.getReplyList(parentId, lastReplyId);

        return ApiResponse.ok(pageResponse);
    }
}
