package com.jj.swm.domain.studyroom.controller;

import com.jj.swm.domain.studyroom.dto.response.GetStudyRoomReviewResponse;
import com.jj.swm.domain.studyroom.service.StudyRoomReviewQueryService;
import com.jj.swm.global.common.dto.ApiResponse;
import com.jj.swm.global.common.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "StudyRoomReview", description = "<b>[스터디 룸 이용후기]</b> API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class StudyRoomReviewQueryController {

    private final StudyRoomReviewQueryService reviewQueryService;

    @Operation(
            summary = "스터디 룸 이용후기 목록 조회",
            description = "스터디 룸 이용후기를 Offset 기반 페이지 조회합니다.\n" +
                    " pageNo의 기본값은 0, onlyImage는 이미지 후기만을 조회할 경우 사용합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "성공"
    )
    @GetMapping("/v1/studyroom/{studyRoomId}/review")
    public ApiResponse<PageResponse<GetStudyRoomReviewResponse>> getStudyRoomReviewList(
            @PathVariable("studyRoomId") Long studyRoomId,
            @RequestParam(value = "pageNo", required = false, defaultValue = "0") int pageNo,
            @RequestParam(value = "onlyImage", required = false, defaultValue = "false") boolean onlyImage
    ) {
        PageResponse<GetStudyRoomReviewResponse> response
                = reviewQueryService.getStudyRoomReviews(studyRoomId, onlyImage, pageNo);

        return ApiResponse.ok(response);
    }
}
