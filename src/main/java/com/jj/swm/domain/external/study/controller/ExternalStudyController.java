package com.jj.swm.domain.external.study.controller;

import com.jj.swm.domain.external.study.dto.response.GetExternalStudiesResponse;
import com.jj.swm.domain.external.study.service.ExternalStudyService;
import com.jj.swm.global.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/external/studies")
@RequiredArgsConstructor
public class ExternalStudyController {
    private final ExternalStudyService externalStudyService;

    @GetMapping
    public ApiResponse<GetExternalStudiesResponse> getExternalStudies(
            @PageableDefault Pageable pageable
    ) {
        return ApiResponse.ok(externalStudyService.getExternalStudies(pageable));
    }
}
