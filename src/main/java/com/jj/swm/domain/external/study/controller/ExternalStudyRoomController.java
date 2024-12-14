package com.jj.swm.domain.external.study.controller;

import com.jj.swm.domain.external.study.dto.response.GetExternalStudyRoomsResponse;
import com.jj.swm.domain.external.study.service.ExternalStudyRoomService;
import com.jj.swm.global.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/external/study")
@RequiredArgsConstructor
public class ExternalStudyRoomController {
    private final ExternalStudyRoomService externalStudyRoomService;

    @Operation(summary = "외부 스터디룸 가져오기")
    @GetMapping()
    public ApiResponse<GetExternalStudyRoomsResponse> getExternalStudies(
            @PageableDefault() Pageable pageable
    ) {
        return ApiResponse.ok(externalStudyRoomService.getExternalStudies(pageable));
    }
}