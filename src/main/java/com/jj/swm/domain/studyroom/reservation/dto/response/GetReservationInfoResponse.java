package com.jj.swm.domain.studyroom.reservation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jj.swm.domain.studyroom.reservation.entity.ApprovalStatus;
import com.jj.swm.domain.studyroom.reservation.repository.custom.ReservationInfoResponse;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class GetReservationInfoResponse {

    private Long studyRoomReservationInfoId;

    private String reserverName;

    private String reserverPhoneNumber;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime checkInTime;

    private String title;

    private ApprovalStatus approvalStatus;

    public static GetReservationInfoResponse from(ReservationInfoResponse reservationInfoResponse) {
        return GetReservationInfoResponse.builder()
                .studyRoomReservationInfoId(reservationInfoResponse.getStudyRoomReservationInfoId())
                .reserverName(reservationInfoResponse.getReserverName())
                .reserverPhoneNumber(reservationInfoResponse.getReserverPhoneNumber())
                .checkInTime(reservationInfoResponse.getCheckInTime())
                .title(reservationInfoResponse.getTitle())
                .approvalStatus(reservationInfoResponse.getApprovalStatus())
                .build();
    }
}
