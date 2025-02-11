package com.jj.swm.domain.studyroom.review.fixture;

import com.jj.swm.domain.studyroom.core.entity.StudyRoom;
import com.jj.swm.domain.studyroom.review.entity.StudyRoomReview;
import com.jj.swm.domain.user.core.entity.User;

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
