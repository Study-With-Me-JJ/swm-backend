package com.jj.swm.domain.studyroom.reservation.repository.custom;

import com.jj.swm.domain.studyroom.reservation.entity.ApprovalStatus;

import java.time.LocalDateTime;

public interface ReservationInfoResponse {

    Long getStudyRoomReservationInfoId();

    String getReserverName();

    String getReserverPhoneNumber();

    LocalDateTime getCheckInTime();

    String getTitle();

    ApprovalStatus getApprovalStatus();
}
