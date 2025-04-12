package com.jj.swm.domain.studyroom.reservation.repository.custom;

import com.jj.swm.domain.studyroom.reservation.entity.ApprovalStatus;

import java.time.LocalDateTime;

public interface ReserverReservationInfoResponse {

    Long getStudyRoomReservationInfoId();

    String getReserverName();

    String getStudyRoomPhoneNumber();

    LocalDateTime getCheckInTime();

    String getTitle();

    ApprovalStatus getApprovalStatus();
}
