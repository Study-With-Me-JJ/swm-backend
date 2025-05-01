package com.jj.swm.domain.studyroom.reservation.fixture;

import com.jj.swm.domain.studyroom.core.entity.StudyRoom;
import com.jj.swm.domain.studyroom.core.entity.StudyRoomReserveType;
import com.jj.swm.domain.studyroom.reservation.entity.ApprovalStatus;
import com.jj.swm.domain.studyroom.reservation.entity.StudyRoomReservationInfo;
import com.jj.swm.domain.user.core.entity.User;

import java.time.LocalDateTime;

public class StudyRoomReservationInfoFixture {

    public static StudyRoomReservationInfo create(
            User user,
            StudyRoom studyRoom,
            StudyRoomReserveType reserveType
    ) {
        LocalDateTime now = LocalDateTime.now();

        return StudyRoomReservationInfo.builder()
                .user(user)
                .studyRoom(studyRoom)
                .studyRoomReserveType(reserveType)
                .reserverName("test")
                .reserverPhoneNumber("010-1234-5678")
                .checkInTime(now)
                .checkOutTime(now.plusHours(3))
                .approvalStatus(ApprovalStatus.WAITING)
                .usageTime(3)
                .headcount(1)
                .memo("memo")
                .totalPrice(9000)
                .build();
    }
}
