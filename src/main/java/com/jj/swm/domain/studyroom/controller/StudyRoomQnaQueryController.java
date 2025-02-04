package com.jj.swm.domain.studyroom.controller;

import com.jj.swm.domain.studyroom.dto.response.GetStudyRoomQnaResponse;
import com.jj.swm.domain.studyroom.service.StudyRoomQnaQueryService;
import com.jj.swm.global.common.dto.ApiResponse;
import com.jj.swm.global.common.dto.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "StudyRoomQna", description = "<b>[스터디 룸 Qna]</b> API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class StudyRoomQnaQueryController {

    private final StudyRoomQnaQueryService qnaQueryService;

    @GetMapping("/v1/studyroom/{studyRoomId}/qna")
    public ApiResponse<PageResponse<GetStudyRoomQnaResponse>> getStudyRoomQnaList(
            @PathVariable("studyRoomId") Long studyRoomId,
            @RequestParam(value = "pageNo", required = false, defaultValue = "0") int pageNo
    ) {
        PageResponse<GetStudyRoomQnaResponse> response = qnaQueryService.getStudyRoomQnas(studyRoomId, pageNo);

        return ApiResponse.ok(response);
    }
}
