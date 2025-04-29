package com.jj.swm.domain.studyroom.reservation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jj.swm.domain.studyroom.reservation.entity.ApprovalStatus;
import com.querydsl.core.annotations.QueryProjection;
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

    @QueryProjection
    public GetReserverReservationInfoResponse(
            Long studyRoomReservationInfoId,
            String reserverName,
            String studyRoomPhoneNumber,
            LocalDateTime checkInTime,
            String title,
            ApprovalStatus approvalStatus
    ) {
        this.studyRoomReservationInfoId = studyRoomReservationInfoId;
        this.reserverName = reserverName;
        this.studyRoomPhoneNumber = studyRoomPhoneNumber;
        this.checkInTime = checkInTime;
        this.title = title;
        this.approvalStatus = approvalStatus;
    }
}
