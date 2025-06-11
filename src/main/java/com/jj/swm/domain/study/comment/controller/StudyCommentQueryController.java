package com.jj.swm.domain.study.comment.controller;

import com.jj.swm.domain.study.comment.dto.response.GetStudyChildCommentResponse;
import com.jj.swm.domain.study.comment.dto.response.GetStudyParentCommentResponse;
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
                    PathVariable로 전달된 스터디 ID에 대한 댓글 목록을 조회합니다.</br>
                    대댓글에 대해서는 해당 댓글에 대한 개수만을 보냅니다.</br>
                    페이지네이션을 기반으로 응답 데이터가 구성됩니다.</br>
                    한 페이지 내에 댓글의 최대 개수는 10개이고, 정렬은 최신순만 지원합니다.
                    """
    )
    public ApiResponse<PageResponse<GetStudyParentCommentResponse>> getParents(
            @PathVariable("studyId") Long studyId,
            @RequestParam(value = "pageNo", required = false, defaultValue = "0") int pageNo
    ) {
        PageResponse<GetStudyParentCommentResponse> pageResponse = commentQueryService.getParents(studyId, pageNo);

        return ApiResponse.ok(pageResponse);
    }

    @GetMapping({"/v1/comment/{commentId}/reply", "/v1/comment/{commentId}/reply/{lastReplyId}"})
    @Operation(
            summary = "스터디 대댓글 목록 조회",
            description = """
                    앞쪽의 PathVariable로 전달된 부모 댓글 ID에 대한 대댓글 목록을 조회합니다.</br>
                    뒤쪽의 PathVariable로 전달된 마지막 대댓글 ID 이후의 대댓글 목록을 조회합니다.</br>
                    무한 스크롤을 기반으로 응답 데이터가 구성됩니다.</br>
                    한 번 대댓글을 조회할 때 대댓글의 최대 개수는 5개이고, 정렬은 최신순만 지원합니다.
                    """
    )
    public ApiResponse<PageResponse<GetStudyChildCommentResponse>> getChildren(
            @PathVariable("commentId") Long parentId,
            @PathVariable(value = "lastReplyId", required = false) Long lastChildId
    ) {
        PageResponse<GetStudyChildCommentResponse> pageResponse = commentQueryService.getChildren(parentId, lastChildId);

        return ApiResponse.ok(pageResponse);
    }
}
