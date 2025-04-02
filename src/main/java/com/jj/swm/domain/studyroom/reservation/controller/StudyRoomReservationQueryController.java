package com.jj.swm.domain.studyroom.reservation.controller;

import com.jj.swm.domain.studyroom.reservation.dto.response.GetReservationInfoDetailsResponse;
import com.jj.swm.domain.studyroom.reservation.service.StudyRoomReservationQueryService;
import com.jj.swm.global.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
            @PathVariable("studyRoomReservationInfoId") Long studyRoomReservationInfoId
    ) {
        GetReservationInfoDetailsResponse response = queryService.getReservationInfoDetails(studyRoomReservationInfoId);

        return ApiResponse.ok(response);
    }
}
