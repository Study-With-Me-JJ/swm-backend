package com.jj.swm.domain.studyroom.controller;


import com.jj.swm.domain.studyroom.dto.GetStudyRoomCondition;
import com.jj.swm.domain.studyroom.dto.response.GetStudyRoomDetailResponse;
import com.jj.swm.domain.studyroom.dto.response.GetStudyRoomResponse;
import com.jj.swm.domain.studyroom.service.StudyRoomQueryService;
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
public class StudyRoomQueryController {

    private final StudyRoomQueryService queryService;

    @GetMapping("/v1/studyroom")
    public ApiResponse<PageResponse<GetStudyRoomResponse>> getStudyRoomList(
            GetStudyRoomCondition condition, Principal principal) {
        PageResponse<GetStudyRoomResponse> response = queryService.getStudyRooms(
                condition, principal != null ? UUID.fromString(principal.getName()) : null);

        return ApiResponse.ok(response);
    }

    @GetMapping("/v1/studyroom/{studyRoomId}")
    public ApiResponse<GetStudyRoomDetailResponse> getStudyRoomDetail(
            @PathVariable("studyRoomId") Long studyRoomId, Principal principal
    ) {
        GetStudyRoomDetailResponse response = queryService.getStudyRoomDetail(
                studyRoomId, principal != null ? UUID.fromString(principal.getName()) : null
        );

        return ApiResponse.ok(response);
    }
}
