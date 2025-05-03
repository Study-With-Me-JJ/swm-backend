package com.jj.swm.domain.studyroom.reservation.controller;

import com.jj.swm.domain.studyroom.reservation.dto.response.GetReservationInfoDetailsResponse;
import com.jj.swm.domain.studyroom.reservation.dto.response.GetOwnerReservationInfoResponse;
import com.jj.swm.domain.studyroom.reservation.dto.response.GetReserverReservationInfoResponse;
import com.jj.swm.domain.studyroom.reservation.entity.ApprovalStatus;
import com.jj.swm.domain.studyroom.reservation.service.StudyRoomReservationQueryService;
import com.jj.swm.global.common.dto.ApiResponse;
import com.jj.swm.global.common.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@Tag(name = "StudyRoomReservationInfo", description = "<b>[스터디 룸 예약]</b> API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class StudyRoomReservationQueryController {

    private final StudyRoomReservationQueryService queryService;

    @Operation(
            summary = "스터디 룸 예약 신청 정보 상세조회 With 토큰",
            description = "스터디 룸 예약 신청 정보를 토큰을 통해 상세조회 합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "성공"
    )
    @GetMapping("/v1/studyroom/reservation/token/{reservationToken}")
    public ApiResponse<GetReservationInfoDetailsResponse> getReservationInfoDetails(
            @PathVariable("reservationToken") String reservationToken
    ) {
        GetReservationInfoDetailsResponse response = queryService.getReservationInfoDetailsWithToken(reservationToken);

        return ApiResponse.ok(response);
    }

    @Operation(
            summary = "스터디 룸 예약 신청 정보 상세조회 With 예약 ID",
            description = "스터디 룸 예약 신청 정보를 예약 ID를 통해 상세조회 합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "성공"
    )
    @GetMapping("/v1/studyroom/reservation/{studyRoomReservationInfoId}")
    public ApiResponse<GetReservationInfoDetailsResponse> getReservationInfoDetails(
            @PathVariable("studyRoomReservationInfoId") Long studyRoomReservationInfoId, Principal principal
    ) {
        GetReservationInfoDetailsResponse response
                = queryService.getReservationInfoDetailsWithId(studyRoomReservationInfoId, UUID.fromString(principal.getName()));

        return ApiResponse.ok(response);
    }

    @Operation(
            summary = "스터디 룸 예약 신청 정보 목록조회 + 승인/거절 상태값(스터디 룸 소유주)",
            description = "스터디 룸 예약 신청 정보를 목록조회 합니다. 상태값으로 승인/거절 전달 기본값(전체조회) <br>" +
                    "스터디 룸 소유주 조회 시"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "성공"
    )
    @Secured("ROLE_ROOM_ADMIN")
    @GetMapping("/v1/studyroom/reservation/owner")
    public ApiResponse<PageResponse<GetOwnerReservationInfoResponse>> getReservationInfosForOwner(
            Principal principal,
            @RequestParam(value = "pageNo", required = false, defaultValue = "0") int pageNo,
            @RequestParam(value = "status", required = false) ApprovalStatus status
    ) {
        PageResponse<GetOwnerReservationInfoResponse> response = queryService.getReservationInfosForOwner(
                UUID.fromString(principal.getName()), pageNo, status
        );

        return ApiResponse.ok(response);
    }

    @Operation(
            summary = "스터디 룸 예약 신청 정보 목록조회 + 승인/거절 상태값(스터디 룸 예약자)",
            description = "스터디 룸 예약 신청 정보를 목록조회 합니다. 상태값으로 승인/거절 전달 기본값(전체조회) <br>" +
                    "스터디 룸 예약자 조회 시"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "성공"
    )
    @GetMapping("/v1/studyroom/reservation/reserver")
    public ApiResponse<PageResponse<GetReserverReservationInfoResponse>> getReservationInfosForReserver(
            Principal principal,
            @RequestParam(value = "pageNo", required = false, defaultValue = "0") int pageNo,
            @RequestParam(value = "status", required = false) ApprovalStatus status
    ) {
        PageResponse<GetReserverReservationInfoResponse> response = queryService.getReservationInfosForReserver(
                UUID.fromString(principal.getName()), pageNo, status
        );

        return ApiResponse.ok(response);
    }
}
