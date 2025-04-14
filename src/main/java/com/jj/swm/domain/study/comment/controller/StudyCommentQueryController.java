package com.jj.swm.domain.study.comment.controller;

import com.jj.swm.domain.study.comment.dto.response.GetStudyCommentResponse;
import com.jj.swm.domain.study.comment.dto.response.GetStudyReplyResponse;
import com.jj.swm.domain.study.comment.service.StudyCommentQueryService;
import com.jj.swm.global.common.dto.ApiResponse;
import com.jj.swm.global.common.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "StudyComment", description = "<b>[스터디 댓글]</b> API")
public class StudyCommentQueryController {

    private final StudyCommentQueryService commentQueryService;

    @GetMapping("/v1/study/{studyId}/comment")
    @Operation(
            summary = "스터디 댓글 목록 조회",
            description = """
                    스터디 댓글을 페이지네이션 기반으로 조회합니다.
                    댓글에 대한 대댓글 개수도 조회합니다.
                    pageNo는 필수값이 아닙니다. 가장 첫 페이지는 pageNo가 0입니다.
                    """
    )
    public ApiResponse<PageResponse<GetStudyCommentResponse>> getComments(
            @PathVariable("studyId") Long studyId,
            @RequestParam(value = "pageNo", required = false, defaultValue = "0") int pageNo
    ) {
        PageResponse<GetStudyCommentResponse> pageResponse = commentQueryService.getComments(studyId, pageNo);

        return ApiResponse.ok(pageResponse);
    }

    @GetMapping({"/v1/comment/{commentId}/reply", "/v1/comment/{commentId}/reply/{lastReplyId}"})
    @Operation(
            summary = "스터디 대댓글 목록 조회",
            description = "스터디 대댓글을 무한 스크롤 기반으로 조회합니다.<br>" +
                    "lastReplyId는 필수 값이 아닙니다. lastReplyId를 보내면 이 다음 대댓글을 불러옵니다."
    )
    public ApiResponse<PageResponse<GetStudyReplyResponse>> getReplies(
            @PathVariable("commentId") Long commentId,
            @PathVariable(value = "lastReplyId", required = false) Long lastReplyId
    ) {
        PageResponse<GetStudyReplyResponse> pageResponse = commentQueryService.getReplies(commentId, lastReplyId);

        return ApiResponse.ok(pageResponse);
    }
}
