package com.jj.swm.domain.studyroom.core.fixture;

import com.jj.swm.domain.studyroom.core.entity.StudyRoom;
import com.jj.swm.domain.studyroom.core.entity.StudyRoomReserveType;

public class StudyRoomReserveTypeFixture {

    public static StudyRoomReserveType create(StudyRoom studyRoom){
        return StudyRoomReserveType.builder()
                .maxHeadcount(3)
                .reservationOption("3인실")
                .pricePerHour(3000)
                .studyRoom(studyRoom)
                .build();
    }
}
