package com.jj.swm.domain.studyroom.dto.response;

import com.jj.swm.domain.studyroom.entity.StudyRoomReview;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateStudyRoomReviewResponse {

    private Long studyRoomReviewId;
    private int rating;
    private double averageRating;

    public static UpdateStudyRoomReviewResponse of(StudyRoomReview studyRoomReview, double averageRating){
        return UpdateStudyRoomReviewResponse.builder()
                .studyRoomReviewId(studyRoomReview.getId())
                .rating(studyRoomReview.getRating())
                .averageRating(averageRating)
                .build();
    }
}
