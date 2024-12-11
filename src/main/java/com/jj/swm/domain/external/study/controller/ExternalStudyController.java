package com.jj.swm.domain.external.study.controller;

import com.jj.swm.domain.external.study.service.ExternalStudyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/external/study")
@RequiredArgsConstructor
public class ExternalStudyController {
    private final ExternalStudyService externalStudyService;
}
