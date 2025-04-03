package com.jj.swm.domain.studyroom.reservation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jj.swm.domain.studyroom.reservation.entity.ApprovalStatus;
import com.jj.swm.domain.studyroom.reservation.entity.StudyRoomReservationInfo;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class GetReservationInfoDetailsResponse {

    private Long studyRoomReservationInfoId;

    private String reserverName;

    private String reserverPhoneNumber;

    private Integer headcount;

    private Integer maxHeadcount;

    private String memo;

    @JsonFormat(pattern = "yyyy.MM.dd HH:mm")
    private LocalDateTime checkInTime;

    private Integer usageTime;

    private ApprovalStatus approvalStatus;

    private String reservationOption;

    private int pricePerHour;

    private Integer totalPrice;

    public static GetReservationInfoDetailsResponse from(StudyRoomReservationInfo studyRoomReservationInfo) {
        return GetReservationInfoDetailsResponse.builder()
                .studyRoomReservationInfoId(studyRoomReservationInfo.getId())
                .reserverName(studyRoomReservationInfo.getReserverName())
                .reserverPhoneNumber(studyRoomReservationInfo.getReserverPhoneNumber())
                .headcount(studyRoomReservationInfo.getHeadcount())
                .maxHeadcount(studyRoomReservationInfo.getStudyRoomReserveType().getMaxHeadcount())
                .memo(studyRoomReservationInfo.getMemo())
                .checkInTime(studyRoomReservationInfo.getCheckInTime())
                .usageTime(studyRoomReservationInfo.getUsageTime())
                .approvalStatus(studyRoomReservationInfo.getApprovalStatus())
                .reservationOption(studyRoomReservationInfo.getStudyRoomReserveType().getReservationOption())
                .pricePerHour(studyRoomReservationInfo.getStudyRoomReserveType().getPricePerHour())
                .totalPrice(studyRoomReservationInfo.getTotalPrice())
                .build();
    }
}
