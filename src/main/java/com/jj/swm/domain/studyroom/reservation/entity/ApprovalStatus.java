package com.jj.swm.domain.studyroom.reservation.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApprovalStatus {
    PENDING("PENDING"), APPROVED("APPROVED"), REJECTED("REJECTED");

    private final String status;
}
