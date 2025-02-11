package com.jj.swm.domain.studyroom.core.dto.response;

import com.jj.swm.domain.studyroom.core.entity.StudyRoomReserveType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetStudyRoomReserveTypeResponse {

    private Long reserveTypeId;
    private int maxHeadcount;
    private String reservationOption;
    private int pricePerHour;

    public static GetStudyRoomReserveTypeResponse from(StudyRoomReserveType reserveType) {
        return GetStudyRoomReserveTypeResponse.builder()
                .reserveTypeId(reserveType.getId())
                .maxHeadcount(reserveType.getMaxHeadcount())
                .reservationOption(reserveType.getReservationOption())
                .pricePerHour(reserveType.getPricePerHour())
                .build();
    }
}
