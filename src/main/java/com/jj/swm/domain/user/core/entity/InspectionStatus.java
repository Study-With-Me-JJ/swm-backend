package com.jj.swm.domain.user.core.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InspectionStatus {
    PENDING("PENDING"), APPROVED("APPROVED"), REJECTED("REJECTED");

    private final String status;
}
