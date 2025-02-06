package com.jj.swm.domain.studyroom.controller;


import com.jj.swm.domain.studyroom.dto.GetStudyRoomCondition;
import com.jj.swm.domain.studyroom.dto.response.GetStudyRoomDetailResponse;
import com.jj.swm.domain.studyroom.dto.response.GetStudyRoomResponse;
import com.jj.swm.domain.studyroom.service.StudyRoomQueryService;
import com.jj.swm.global.common.dto.ApiResponse;
import com.jj.swm.global.common.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.UUID;

@Tag(name = "StudyRoom", description = "<b>[스터디 룸]</b> API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class StudyRoomQueryController {

    private final StudyRoomQueryService queryService;

    @Operation(
            summary = "스터디 룸 목록 조회",
            description = "사용자의 위치, 가격, 정렬 기준 등을 기반으로 스터디 룸을 필터링하여 Cursor 기반 페이지 조회합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "성공"
    )
    @GetMapping("/v1/studyroom")
    public ApiResponse<PageResponse<GetStudyRoomResponse>> getStudyRoomList(
            @Valid GetStudyRoomCondition condition, Principal principal) {
        PageResponse<GetStudyRoomResponse> response = queryService.getStudyRooms(
                condition, principal != null ? UUID.fromString(principal.getName()) : null);

        return ApiResponse.ok(response);
    }

    @Operation(
            summary = "스터디 룸 상세 조회",
            description = "스터디 룸을 상세 조회합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "성공"
    )
    @GetMapping("/v1/studyroom/{studyRoomId}")
    public ApiResponse<GetStudyRoomDetailResponse> getStudyRoomDetail(
            @PathVariable("studyRoomId") Long studyRoomId, Principal principal
    ) {
        GetStudyRoomDetailResponse response = queryService.getStudyRoomDetail(
                studyRoomId, principal != null ? UUID.fromString(principal.getName()) : null);

        return ApiResponse.ok(response);
    }
}
