package com.jj.swm.domain.studyroom.reservation.dto.event;

import com.jj.swm.domain.studyroom.reservation.entity.StudyRoomReservationInfo;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class StudyRoomReservationRequestEvent {

    private Long studyRoomReservationInfoId;
    private String reserverName;
    private String reserverPhoneNumber;
    private Integer headcount;
    private String memo;
    private LocalDateTime checkInTime;
    private Integer usageTime;
    private int maxHeadcount;
    private String reservationOption;
    private int pricePerHour;

    public static StudyRoomReservationRequestEvent from(StudyRoomReservationInfo studyRoomReservationInfo) {
        return StudyRoomReservationRequestEvent.builder()
                .reserverName(studyRoomReservationInfo.getReserverName())
                .reserverPhoneNumber(studyRoomReservationInfo.getReserverPhoneNumber())
                .headcount(studyRoomReservationInfo.getHeadcount())
                .memo(studyRoomReservationInfo.getMemo())
                .checkInTime(studyRoomReservationInfo.getCheckInTime())
                .usageTime(studyRoomReservationInfo.getUsageTime())
                .maxHeadcount(studyRoomReservationInfo.getStudyRoomReserveType().getMaxHeadcount())
                .reservationOption(studyRoomReservationInfo.getStudyRoomReserveType().getReservationOption())
                .pricePerHour(studyRoomReservationInfo.getStudyRoomReserveType().getPricePerHour())
                .build();
    }
}
