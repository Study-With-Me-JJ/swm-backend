package com.jj.swm.domain.studyroom.core.fixture;

import com.jj.swm.domain.studyroom.core.entity.StudyRoom;
import com.jj.swm.domain.studyroom.core.entity.StudyRoomBookmark;
import com.jj.swm.domain.user.core.entity.User;

public class StudyRoomBookmarkFixture {

    public static StudyRoomBookmark createBookmark(StudyRoom studyRoom, User user){
        return StudyRoomBookmark.builder()
                .studyRoom(studyRoom)
                .user(user)
                .build();
    }
}
