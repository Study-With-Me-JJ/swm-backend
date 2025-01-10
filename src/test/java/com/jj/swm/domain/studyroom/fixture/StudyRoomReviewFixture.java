package com.jj.swm.domain.studyroom.fixture;

import com.jj.swm.domain.studyroom.entity.StudyRoom;
import com.jj.swm.domain.studyroom.entity.StudyRoomReview;
import com.jj.swm.domain.user.entity.User;

public class StudyRoomReviewFixture {

    public static StudyRoomReview createReview(
            StudyRoom studyRoom,
            int rate,
            User user
    ){
        return StudyRoomReview.builder()
                .studyRoom(studyRoom)
                .rating(rate)
                .comment("test")
                .user(user)
                .build();
    }
}
