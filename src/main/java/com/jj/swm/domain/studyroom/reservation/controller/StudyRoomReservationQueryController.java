package com.jj.swm.domain.studyroom.reservation.controller;

import com.jj.swm.domain.studyroom.reservation.dto.response.GetReservationInfoDetailsResponse;
import com.jj.swm.domain.studyroom.reservation.dto.response.GetOwnerReservationInfoResponse;
import com.jj.swm.domain.studyroom.reservation.dto.response.GetReserverReservationInfoResponse;
import com.jj.swm.domain.studyroom.reservation.entity.ApprovalStatus;
import com.jj.swm.domain.studyroom.reservation.service.StudyRoomReservationQueryService;
import com.jj.swm.global.common.dto.ApiResponse;
import com.jj.swm.global.common.dto.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Tag(name = "StudyRoomReservationInfo", description = "<b>[스터디 룸 예약]</b> API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class StudyRoomReservationQueryController {

    private final StudyRoomReservationQueryService queryService;

    @GetMapping("/v1/studyroom/reservation/token/{reservationToken}")
    public ApiResponse<GetReservationInfoDetailsResponse> getReservationInfoDetails(
            @PathVariable("reservationToken") String reservationToken
    ) {
        GetReservationInfoDetailsResponse response = queryService.getReservationInfoDetails(reservationToken);

        return ApiResponse.ok(response);
    }

    @GetMapping("/v1/studyroom/reservation/{studyRoomReservationInfoId}")
    public ApiResponse<GetReservationInfoDetailsResponse> getReservationInfoDetails(
            @PathVariable("studyRoomReservationInfoId") Long studyRoomReservationInfoId, Principal principal
    ) {
        GetReservationInfoDetailsResponse response
                = queryService.getReservationInfoDetails(studyRoomReservationInfoId, UUID.fromString(principal.getName()));

        return ApiResponse.ok(response);
    }

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
