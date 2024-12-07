package com.jj.swm.domain.study.controller;

import com.jj.swm.domain.study.dto.StudyInquiryCondition;
import com.jj.swm.domain.study.dto.response.StudyInquiryResponse;
import com.jj.swm.domain.study.service.StudyQueryService;
import com.jj.swm.global.common.dto.ApiResponse;
import com.jj.swm.global.common.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

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
            @RequestParam(value = "page", defaultValue = "0") int pageNo,
            @Value("${study.page.size}") int pageSize,
            StudyInquiryCondition inquiryConditionRequest
    ) {
        PageResponse<StudyInquiryResponse> pageResponse = studyQueryService.getList(
                principal != null ? UUID.fromString(principal.getName()) : null,
                pageNo,
                pageSize,
                inquiryConditionRequest
        );

        return ApiResponse.ok(pageResponse);
    }
}
