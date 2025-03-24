package com.jj.swm.domain.studyroom.reservation.controller;

import com.jj.swm.domain.studyroom.reservation.dto.request.CreateStudyRoomReservationRequest;
import com.jj.swm.domain.studyroom.reservation.dto.request.UpdateStudyRoomReservationApprovalStatusRequest;
import com.jj.swm.domain.studyroom.reservation.service.StudyRoomReservationCommandService;
import com.jj.swm.global.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@Tag(name = "StudyRoomReservationInfo", description = "<b>[스터디 룸 예약]</b> API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class StudyRoomReservationCommandController {

    private final StudyRoomReservationCommandService commandService;

    @PostMapping("/v1/studyroom/reservation")
    public ApiResponse<Void> createStudyRoomReservation(
            @Valid @RequestBody CreateStudyRoomReservationRequest request, Principal principal
    ) {
        commandService.createStudyRoomReservationAndSendSms(request, UUID.fromString(principal.getName()));

        return ApiResponse.created(null);
    }

    @PatchMapping("/v1/studyroom/reservation/owner/{studyRoomReservationInfoId}")
    public ApiResponse<Void> updateStudyRoomReservationApprovalStatus(
            @Valid @RequestBody UpdateStudyRoomReservationApprovalStatusRequest request,
            @PathVariable("studyRoomReservationInfoId") Long studyRoomReservationInfoId
    ) {
        commandService.updateStudyRoomReservationApprovalStatusAndSendSms(request, studyRoomReservationInfoId);

        return ApiResponse.created(null);
    }
}
