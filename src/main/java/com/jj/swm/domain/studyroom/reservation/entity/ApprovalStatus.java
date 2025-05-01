package com.jj.swm.domain.studyroom.reservation.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApprovalStatus {
    WAITING("WAITING"), APPROVED("APPROVED"), REJECTED("REJECTED"), CANCELED("CANCELED");

    private final String status;
}
