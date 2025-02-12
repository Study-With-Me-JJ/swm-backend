package com.jj.swm.domain.studyroom.core.fixture;

import com.jj.swm.domain.studyroom.core.entity.StudyRoom;
import com.jj.swm.domain.studyroom.core.entity.StudyRoomOption;
import com.jj.swm.domain.studyroom.core.entity.StudyRoomOptionInfo;

public class StudyRoomOptionInfoFixture {

    public static StudyRoomOptionInfo createOptionInfo(StudyRoom studyRoom){
        return StudyRoomOptionInfo.builder()
                .option(StudyRoomOption.ELECTRICAL)
                .studyRoom(studyRoom)
                .build();
    }
}
