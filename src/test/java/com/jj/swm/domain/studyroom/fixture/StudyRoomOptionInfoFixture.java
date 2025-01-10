package com.jj.swm.domain.studyroom.fixture;

import com.jj.swm.domain.studyroom.entity.StudyRoom;
import com.jj.swm.domain.studyroom.entity.StudyRoomOption;
import com.jj.swm.domain.studyroom.entity.StudyRoomOptionInfo;

public class StudyRoomOptionInfoFixture {

    public static StudyRoomOptionInfo createOptionInfo(StudyRoom studyRoom){
        return StudyRoomOptionInfo.builder()
                .option(StudyRoomOption.ELECTRICAL)
                .studyRoom(studyRoom)
                .build();
    }
}
