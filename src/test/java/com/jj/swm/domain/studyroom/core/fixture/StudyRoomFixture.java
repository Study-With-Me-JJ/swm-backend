package com.jj.swm.domain.studyroom.core.fixture;

import com.jj.swm.domain.studyroom.core.entity.StudyRoom;
import com.jj.swm.domain.studyroom.core.entity.embeddable.Address;
import com.jj.swm.domain.studyroom.core.entity.embeddable.Coordinates;
import com.jj.swm.domain.user.core.entity.User;

import java.time.LocalTime;
import java.util.concurrent.atomic.AtomicInteger;

public class StudyRoomFixture {

    public static AtomicInteger count = new AtomicInteger(1);
    public static AtomicInteger distanceCount = new AtomicInteger(1);

    public static StudyRoom create(User user) {
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
                .coordinates(Coordinates.builder()
                        .latitude(calculateDistance())
                        .longitude(distanceCount.doubleValue())
                        .build())
                .thumbnail("http://test.png")
                .referenceUrl("http://test.com")
                .phoneNumber("010-0000-0000")
                .minReserveTime(2)
                .entireMinHeadcount(1)
                .entireMaxHeadcount(2)
                .entireMinPricePerHour(count.getAndIncrement() * 1000)
                .entireMaxPricePerHour(count.getAndIncrement() * 10000)
                .user(user)
                .deletedAt(null)
                .build();
    }

    private static double calculateDistance() {
        distanceCount.incrementAndGet();

        return distanceCount.doubleValue();
    }
}
