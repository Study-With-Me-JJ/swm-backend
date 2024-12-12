package com.jj.swm.domain.studyroom;

import com.jj.swm.domain.studyroom.entity.StudyRoom;
import com.jj.swm.domain.studyroom.entity.StudyRoomQna;
import com.jj.swm.domain.user.entity.User;

public class StudyRoomQnaFixture {

    public static StudyRoomQna createStudyRoomQna(StudyRoom studyRoom, User user){
        return StudyRoomQna.builder()
                .comment("test")
                .studyRoom(studyRoom)
                .user(user)
                .build();
    }
}
