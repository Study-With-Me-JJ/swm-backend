package com.jj.swm.domain.studyroom.core.fixture;

import com.jj.swm.domain.studyroom.core.entity.StudyRoom;
import com.jj.swm.domain.studyroom.core.entity.StudyRoomLike;
import com.jj.swm.domain.user.core.entity.User;

public class StudyRoomLikeFixture {

    public static StudyRoomLike createLike(StudyRoom studyRoom, User user){
        return StudyRoomLike.builder()
                .studyRoom(studyRoom)
                .user(user)
                .build();
    }
}
