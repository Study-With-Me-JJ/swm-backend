package com.jj.swm.domain.studyroom.core.controller;


import com.jj.swm.domain.studyroom.core.dto.GetStudyRoomCondition;
import com.jj.swm.domain.studyroom.core.dto.response.GetStudyRoomDetailResponse;
import com.jj.swm.domain.studyroom.core.dto.response.GetStudyRoomResponse;
import com.jj.swm.domain.studyroom.core.service.StudyRoomQueryService;
import com.jj.swm.global.common.dto.ApiResponse;
import com.jj.swm.global.common.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
            description = "사용자의 위치, 가격, 정렬 기준 등을 기반으로 스터디 룸을 필터링하여 Cursor 기반 페이지 조회합니다.<br><br>" +
                    "<b>[정렬 별 활용 필드 - lastStudyRoomId 필수]</b><br> " +
                    "STAR: req -> lastAverageRatingValue, res -> starAvg<br> " +
                    "(LIKE, REVIEW, PRICE_ASC, PRICE_DESC): req -> lastSortValue, " +
                    "res -> likeCount, reviewCount, entireMinPricePerHour, entireMaxPricePerHour<br>" +
                    "DISTANCE: req -> lastLatitudeValue, lastLongitudeValue, res -> coordinates"
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

    @Operation(
            summary = "특정 유저가 좋아요한 스터디 룸 목록 조회",
            description = "특정 유저가 좋아요한 스터디 룸을 페이징 조회합니다." +
                    "pageNo는 필수값은 아니지만, 기본값인 0으로 고정됩니다.<br>" +
                    "그 이후 페이지 번호에 관해서는 Query Param으로 전달 부탁드립니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "성공"
    )
    @GetMapping("/v1/studyroom/user/liked-studyrooms")
    public ApiResponse<PageResponse<GetStudyRoomResponse>> getUserLikedStudyRoomList(
            @RequestParam(value = "pageNo", required = false, defaultValue = "0") int pageNo,
            Principal principal
    ) {
        PageResponse<GetStudyRoomResponse> response = queryService.getLikedStudyRooms(
                pageNo, UUID.fromString(principal.getName())
        );

        return ApiResponse.ok(response);
    }

    @Operation(
            summary = "특정 유저가 북마크한 스터디 룸 목록 조회",
            description = "특정 유저가 북마크한 스터디 룸을 페이징 조회합니다." +
                    "pageNo는 필수값은 아니지만, 기본값인 0으로 고정됩니다.<br>" +
                    "그 이후 페이지 번호에 관해서는 Query Param으로 전달 부탁드립니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "성공"
    )
    @GetMapping("/v1/studyroom/user/bookmarked-studyrooms")
    public ApiResponse<PageResponse<GetStudyRoomResponse>> getUserBookmarkedStudyRoomList(
            @RequestParam(value = "pageNo", required = false, defaultValue = "0") int pageNo,
            Principal principal
    ) {
        PageResponse<GetStudyRoomResponse> response = queryService.getBookmarkedStudyRooms(
                pageNo, UUID.fromString(principal.getName())
        );

        return ApiResponse.ok(response);
    }
}
