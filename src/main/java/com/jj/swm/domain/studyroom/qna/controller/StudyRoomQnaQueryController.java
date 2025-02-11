package com.jj.swm.domain.studyroom.qna.controller;

import com.jj.swm.domain.studyroom.qna.dto.response.GetStudyRoomQnaResponse;
import com.jj.swm.domain.studyroom.qna.service.StudyRoomQnaQueryService;
import com.jj.swm.global.common.dto.ApiResponse;
import com.jj.swm.global.common.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "StudyRoomQna", description = "<b>[스터디 룸 Qna]</b> API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class StudyRoomQnaQueryController {

    private final StudyRoomQnaQueryService qnaQueryService;

    @Operation(
            summary = "스터디 룸 QnA 목록 조회",
            description = "스터디 룸 QnA를 Offset 기반 페이지 조회합니다. 대댓글도 함께 조회됩니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "성공"
    )
    @GetMapping("/v1/studyroom/{studyRoomId}/qna")
    public ApiResponse<PageResponse<GetStudyRoomQnaResponse>> getStudyRoomQnaList(
            @PathVariable("studyRoomId") Long studyRoomId,
            @RequestParam(value = "pageNo", required = false, defaultValue = "0") int pageNo
    ) {
        PageResponse<GetStudyRoomQnaResponse> response = qnaQueryService.getStudyRoomQnas(studyRoomId, pageNo);

        return ApiResponse.ok(response);
    }
}
