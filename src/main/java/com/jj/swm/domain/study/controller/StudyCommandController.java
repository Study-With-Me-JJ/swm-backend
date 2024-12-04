package com.jj.swm.domain.study.controller;

import com.jj.swm.domain.study.dto.request.StudyCreateRequest;
import com.jj.swm.domain.study.service.StudyCommandService;
import com.jj.swm.global.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class StudyCommandController {

    private final StudyCommandService studyCommandService;

    @PostMapping("/v1/study")
    public ApiResponse<Void> createStudy(
            Principal principal, @Valid @RequestBody StudyCreateRequest createRequest
    ) {
        studyCommandService.create(
                UUID.fromString(principal.getName()), createRequest
        );

        return ApiResponse.created(null);
    }
}
