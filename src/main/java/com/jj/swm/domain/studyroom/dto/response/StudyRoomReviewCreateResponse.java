package com.jj.swm.domain.studyroom.dto.response;

import com.jj.swm.domain.studyroom.entity.StudyRoomReview;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StudyRoomReviewCreateResponse {

    private Long studyRoomReviewId;
    private int rating;
    private double averageRating;

    public static StudyRoomReviewCreateResponse from(StudyRoomReview studyRoomReview) {
        return StudyRoomReviewCreateResponse.builder()
                .studyRoomReviewId(studyRoomReview.getId())
                .rating(studyRoomReview.getRating())
                .averageRating(studyRoomReview.getStudyRoom().getAverageRating())
                .build();
    }
}
