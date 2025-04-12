package com.jj.swm.domain.studyroom.reservation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jj.swm.domain.studyroom.reservation.entity.ApprovalStatus;
import com.jj.swm.domain.studyroom.reservation.repository.custom.OwnerReservationInfoResponse;
import com.jj.swm.domain.studyroom.reservation.repository.custom.ReserverReservationInfoResponse;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class GetReserverReservationInfoResponse {

    private Long studyRoomReservationInfoId;

    private String reserverName;

    private String studyRoomPhoneNumber;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime checkInTime;

    private String title;

    private ApprovalStatus approvalStatus;

    public static GetReserverReservationInfoResponse from(ReserverReservationInfoResponse reservationInfoResponse) {
        return GetReserverReservationInfoResponse.builder()
                .studyRoomReservationInfoId(reservationInfoResponse.getStudyRoomReservationInfoId())
                .reserverName(reservationInfoResponse.getReserverName())
                .studyRoomPhoneNumber(reservationInfoResponse.getStudyRoomPhoneNumber())
                .checkInTime(reservationInfoResponse.getCheckInTime())
                .title(reservationInfoResponse.getTitle())
                .approvalStatus(reservationInfoResponse.getApprovalStatus())
                .build();
    }
}
