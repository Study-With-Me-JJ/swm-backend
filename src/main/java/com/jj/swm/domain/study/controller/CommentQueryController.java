package com.jj.swm.domain.study.controller;

import com.jj.swm.domain.study.dto.response.CommentInquiryResponse;
import com.jj.swm.domain.study.service.CommentQueryService;
import com.jj.swm.global.common.dto.ApiResponse;
import com.jj.swm.global.common.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentQueryController {

    private final CommentQueryService commentQueryService;

    @GetMapping("/v1/study/{studyId}/comment")
    public ApiResponse<PageResponse<CommentInquiryResponse>> getCommentList(
            @PathVariable("studyId") Long studyId, @RequestParam("pageNo") int pageNo
    ) {
        PageResponse<CommentInquiryResponse> pageResponse = commentQueryService.getList(studyId, pageNo);

        return ApiResponse.ok(pageResponse);
    }
}
