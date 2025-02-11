package com.jj.swm.domain.study.controller.core;

import com.jj.swm.domain.study.dto.core.StudyInquiryCondition;
import com.jj.swm.domain.study.dto.core.response.StudyDetailsResponse;
import com.jj.swm.domain.study.dto.core.response.StudyInquiryResponse;
import com.jj.swm.domain.study.service.core.StudyQueryService;
import com.jj.swm.global.common.dto.ApiResponse;
import com.jj.swm.global.common.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
            Principal principal, StudyInquiryCondition inquiryCondition
    ) {
        PageResponse<StudyInquiryResponse> pageResponse = studyQueryService.getList(
                principal != null ? UUID.fromString(principal.getName()) : null, inquiryCondition
        );

        return ApiResponse.ok(pageResponse);
    }

    @GetMapping("/v1/study/{studyId}")
    public ApiResponse<StudyDetailsResponse> getStudyDetails(
            Principal principal, @PathVariable("studyId") Long studyId
    ) {
        StudyDetailsResponse detailsResponse = studyQueryService.get(
                principal != null ? UUID.fromString(principal.getName()) : null, studyId
        );

        return ApiResponse.ok(detailsResponse);
    }
}
