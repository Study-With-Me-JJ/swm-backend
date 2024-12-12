package com.jj.swm.domain.study.controller;

import com.jj.swm.domain.study.dto.StudyInquiryCondition;
import com.jj.swm.domain.study.dto.response.StudyInquiryResponse;
import com.jj.swm.domain.study.service.StudyQueryService;
import com.jj.swm.global.common.dto.ApiResponse;
import com.jj.swm.global.common.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class StudyQueryController {

    private final StudyQueryService studyQueryService;

    @GetMapping("/v1/study")
    public ApiResponse<PageResponse<StudyInquiryResponse>> getStudyList(
            Principal principal,
            StudyInquiryCondition inquiryCondition
    ) {
        PageResponse<StudyInquiryResponse> pageResponse = studyQueryService.getList(
//                UUID.fromString("d554b429-366f-4d8e-929d-bb5479623eb9"),
                                principal != null ? UUID.fromString(principal.getName()) : null,
                inquiryCondition
        );

        return ApiResponse.ok(pageResponse);
    }
}
