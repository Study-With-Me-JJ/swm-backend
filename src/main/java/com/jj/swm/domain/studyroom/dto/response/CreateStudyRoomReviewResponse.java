package com.jj.swm.domain.studyroom.dto.response;

import com.jj.swm.domain.studyroom.entity.StudyRoomReview;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateStudyRoomReviewResponse {

    private Long studyRoomReviewId;
    private int rating;
    private double averageRating;

    public static CreateStudyRoomReviewResponse from(StudyRoomReview studyRoomReview) {
        return CreateStudyRoomReviewResponse.builder()
                .studyRoomReviewId(studyRoomReview.getId())
                .rating(studyRoomReview.getRating())
                .averageRating(studyRoomReview.getStudyRoom().getAverageRating())
                .build();
    }
}
