package com.jj.swm.domain.studyroom.qna.fixture;

import com.jj.swm.domain.studyroom.core.entity.StudyRoom;
import com.jj.swm.domain.studyroom.qna.entity.StudyRoomQna;
import com.jj.swm.domain.user.core.entity.User;

public class StudyRoomQnaFixture {

    public static StudyRoomQna createStudyRoomQna(StudyRoom studyRoom, User user){
        return StudyRoomQna.builder()
                .comment("test")
                .studyRoom(studyRoom)
                .user(user)
                .build();
    }
}
