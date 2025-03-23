package com.jj.swm.domain.studyroom.reservation.controller;

import com.jj.swm.domain.studyroom.reservation.dto.request.CreateStudyRoomReservationRequest;
import com.jj.swm.domain.studyroom.reservation.service.StudyRoomReservationCommandService;
import com.jj.swm.global.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.UUID;

@Tag(name = "StudyRoomReservationInfo", description = "<b>[스터디 룸 예약]</b> API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class StudyRoomReservationCommandController {

    private final StudyRoomReservationCommandService commandService;

    @PostMapping("/v1/studyroom/reservation")
    public ApiResponse<Void> createStudyRoomReservation(CreateStudyRoomReservationRequest request, Principal principal){
        commandService.createStudyRoomReservationAndSendSms(request, UUID.fromString(principal.getName()));

        return ApiResponse.created(null);
    }
}
