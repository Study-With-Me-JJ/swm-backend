package com.jj.swm.domain.studyroom;

import com.jj.swm.domain.studyroom.entity.StudyRoom;
import com.jj.swm.domain.studyroom.entity.embeddable.Address;
import com.jj.swm.domain.studyroom.entity.embeddable.Point;
import com.jj.swm.domain.user.entity.User;

import java.time.LocalTime;

public class StudyRoomFixture {

    public static StudyRoom createStudyRoom(User user) {
        return StudyRoom.builder()
                .id(1L)
                .title("test")
                .subtitle("test")
                .introduce("test")
                .notice("test")
                .guideline("test")
                .openingTime(LocalTime.MIN)
                .closingTime(LocalTime.MAX)
                .address(Address.builder()
                        .address("서울 동작구")
                        .detailAddress("서울 동작구 23번길")
                        .region("서울")
                        .locality("동작구")
                        .build())
                .point(Point.builder()
                        .latitude(0.0)
                        .longitude(0.0)
                        .build())
                .thumbnail("http://test.png")
                .referenceUrl("http://test.com")
                .phoneNumber("010-0000-0000")
                .minReserveTime(2)
                .entireMinHeadcount(1)
                .entireMaxHeadcount(2)
                .entireMinPricePerHour(1000)
                .entireMaxPricePerHour(10000)
                .user(user)
                .build();
    }

    public static StudyRoom createStudyRoomWithoutId(User user) {
        return StudyRoom.builder()
                .title("test")
                .subtitle("test")
                .introduce("test")
                .notice("test")
                .guideline("test")
                .openingTime(LocalTime.MIN)
                .closingTime(LocalTime.MAX)
                .address(Address.builder()
                        .address("서울 동작구")
                        .detailAddress("서울 동작구 23번길")
                        .region("서울")
                        .locality("동작구")
                        .build())
                .point(Point.builder()
                        .latitude(0.0)
                        .longitude(0.0)
                        .build())
                .thumbnail("http://test.png")
                .referenceUrl("http://test.com")
                .phoneNumber("010-0000-0000")
                .minReserveTime(2)
                .entireMinHeadcount(1)
                .entireMaxHeadcount(2)
                .entireMinPricePerHour(1000)
                .entireMaxPricePerHour(10000)
                .user(user)
                .deletedAt(null)
                .build();
    }
}
