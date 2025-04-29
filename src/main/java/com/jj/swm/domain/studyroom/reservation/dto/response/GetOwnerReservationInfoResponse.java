package com.jj.swm.domain.studyroom.reservation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jj.swm.domain.studyroom.reservation.entity.ApprovalStatus;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class GetOwnerReservationInfoResponse {

    private Long studyRoomReservationInfoId;

    private String reserverName;

    private String reserverPhoneNumber;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime checkInTime;

    private String title;

    private ApprovalStatus approvalStatus;

    @QueryProjection
    public GetOwnerReservationInfoResponse(
            Long studyRoomReservationInfoId,
            String reserverName,
            String reserverPhoneNumber,
            LocalDateTime checkInTime,
            String title,
            ApprovalStatus approvalStatus
    ) {
        this.studyRoomReservationInfoId = studyRoomReservationInfoId;
        this.reserverName = reserverName;
        this.reserverPhoneNumber = reserverPhoneNumber;
        this.checkInTime = checkInTime;
        this.title = title;
        this.approvalStatus = approvalStatus;
    }
}
