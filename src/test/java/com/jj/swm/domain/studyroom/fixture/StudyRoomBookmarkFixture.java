package com.jj.swm.domain.studyroom.fixture;

import com.jj.swm.domain.studyroom.entity.StudyRoom;
import com.jj.swm.domain.studyroom.entity.StudyRoomBookmark;
import com.jj.swm.domain.user.entity.User;

public class StudyRoomBookmarkFixture {

    public static StudyRoomBookmark createBookmark(StudyRoom studyRoom, User user){
        return StudyRoomBookmark.builder()
                .studyRoom(studyRoom)
                .user(user)
                .build();
    }
}
