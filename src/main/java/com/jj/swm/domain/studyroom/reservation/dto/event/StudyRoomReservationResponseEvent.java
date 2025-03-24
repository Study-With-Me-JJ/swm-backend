package com.jj.swm.domain.studyroom.reservation.dto.event;

import com.jj.swm.domain.studyroom.reservation.entity.StudyRoomReservationInfo;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class StudyRoomReservationResponseEvent {

    private String title;
    private String reservationOption;
    private String reserverName;
    private String reserverPhoneNumber;
    private String roomAdminPhoneNumber;
    private Integer headcount;
    private String memo;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private Integer usageTime;
    private int totalPrice;

    public static StudyRoomReservationResponseEvent from(StudyRoomReservationInfo studyRoomReservationInfo) {
        return StudyRoomReservationResponseEvent.builder()
                .title(studyRoomReservationInfo.getStudyRoom().getTitle())
                .reservationOption(studyRoomReservationInfo.getStudyRoomReserveType().getReservationOption())
                .reserverName(studyRoomReservationInfo.getReserverName())
                .reserverPhoneNumber(studyRoomReservationInfo.getReserverPhoneNumber())
                .roomAdminPhoneNumber(studyRoomReservationInfo.getStudyRoom().getPhoneNumber())
                .headcount(studyRoomReservationInfo.getHeadcount())
                .memo(studyRoomReservationInfo.getMemo())
                .checkInTime(studyRoomReservationInfo.getCheckInTime())
                .checkOutTime(studyRoomReservationInfo.getCheckOutTime())
                .usageTime(studyRoomReservationInfo.getUsageTime())
                .totalPrice(studyRoomReservationInfo.getTotalPrice())
                .build();
    }
}
