package com.jj.swm.domain.studyroom.fixture;

import com.jj.swm.domain.studyroom.entity.StudyRoom;
import com.jj.swm.domain.studyroom.entity.StudyRoomLike;
import com.jj.swm.domain.user.entity.User;

public class StudyRoomLikeFixture {

    public static StudyRoomLike createLike(StudyRoom studyRoom, User user){
        return StudyRoomLike.builder()
                .studyRoom(studyRoom)
                .user(user)
                .build();
    }
}
