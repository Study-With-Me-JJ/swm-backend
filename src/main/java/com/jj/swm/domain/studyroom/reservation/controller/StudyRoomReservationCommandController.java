package com.jj.swm.domain.studyroom.reservation.controller;

import com.jj.swm.domain.studyroom.reservation.dto.request.CreateStudyRoomReservationRequest;
import com.jj.swm.domain.studyroom.reservation.dto.request.UpdateStudyRoomReservationApprovalStatusRequest;
import com.jj.swm.domain.studyroom.reservation.dto.request.UpdateStudyRoomReservationRequest;
import com.jj.swm.domain.studyroom.reservation.service.StudyRoomReservationCommandService;
import com.jj.swm.global.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@Tag(name = "StudyRoomReservationInfo", description = "<b>[스터디 룸 예약]</b> API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class StudyRoomReservationCommandController {

    private final StudyRoomReservationCommandService commandService;

    @Operation(
            summary = "스터디 룸 예약 생성",
            description = "스터디 룸 예약을 생성합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201", description = "성공"
    )
    @PostMapping("/v1/studyroom/reservation")
    public ApiResponse<Void> createStudyRoomReservation(
            @Valid @RequestBody CreateStudyRoomReservationRequest request, Principal principal
    ) {
        commandService.createStudyRoomReservationAndSendSms(request, UUID.fromString(principal.getName()));

        return ApiResponse.created(null);
    }

    @Operation(
            summary = "스터디 룸 예약 신청을 승인/거절 With 토큰",
            description = "스터디 룸 예약 신청을 토큰을 통해 승인/거절 합니다. 인증 X"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "성공"
    )
    @PatchMapping("/v1/studyroom/reservation/token/{reservationToken}/status")
    public ApiResponse<Void> updateStudyRoomReservationApprovalStatus(
            @Valid @RequestBody UpdateStudyRoomReservationApprovalStatusRequest request,
            @PathVariable("reservationToken") String reservationToken
    ) {
        commandService.updateStudyRoomReservationApprovalStatusAndSendSms(request, reservationToken);

        return ApiResponse.created(null);
    }

    @Operation(
            summary = "스터디 룸 예약 신청을 승인/거절 With 예약 ID",
            description = "스터디 룸 예약 신청 예약 ID를 통해 승인/거절 합니다. 인증 O"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "성공"
    )
    @Secured("ROLE_ROOM_ADMIN")
    @PatchMapping("/v1/studyroom/reservation/{studyRoomReservationInfoId}/status")
    public ApiResponse<Void> updateStudyRoomReservationApprovalStatus(
            @Valid @RequestBody UpdateStudyRoomReservationApprovalStatusRequest request,
            @PathVariable("studyRoomReservationInfoId") Long studyRoomReservationInfoId
    ) {
        commandService.updateStudyRoomReservationApprovalStatusAndSendSms(request, studyRoomReservationInfoId);

        return ApiResponse.ok(null);
    }

    @Operation(
            summary = "스터디 룸 예약 신청 정보 변경",
            description = "스터디 룸 예약 신청 정보를 변경합니다.(예약 신청자)"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "성공"
    )
    @PatchMapping("/v1/studyroom/reservation/{studyRoomReservationInfoId}")
    public ApiResponse<Void> updateStudyRoomReservation(
            @Valid @RequestBody UpdateStudyRoomReservationRequest request,
            @PathVariable("studyRoomReservationInfoId") Long studyRoomReservationInfoId,
            Principal principal
    ) {
        commandService.updateStudyRoomReservation(
                request,
                studyRoomReservationInfoId,
                UUID.fromString(principal.getName())
        );

        return ApiResponse.ok(null);
    }

    @Operation(
            summary = "스터디 룸 예약 신청 취소",
            description = "스터디 룸 예약 신청을 취소합니다.(예약 신청자)"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "성공"
    )
    @PatchMapping("/v1/studyroom/reservation/{studyRoomReservationInfoId}/cancel")
    public ApiResponse<Void> cancelStudyRoomReservation(
            @PathVariable("studyRoomReservationInfoId") Long studyRoomReservationInfoId, Principal principal
    ) {
      commandService.cancelStudyRoomReservation(studyRoomReservationInfoId, UUID.fromString(principal.getName()));

      return ApiResponse.ok(null);
    }
}
