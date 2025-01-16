package com.jj.swm.domain.external.study.controller;

import com.jj.swm.domain.external.study.dto.response.GetExternalStudiesResponse;
import com.jj.swm.domain.external.study.service.ExternalStudyService;
import com.jj.swm.global.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
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

    @Operation(summary = "외부 스터디 가져오기")
    @GetMapping
    public ApiResponse<GetExternalStudiesResponse> getExternalStudies(
            @PageableDefault @ParameterObject Pageable pageable
    ) {
        return ApiResponse.ok(externalStudyService.getExternalStudies(pageable));
    }
}
