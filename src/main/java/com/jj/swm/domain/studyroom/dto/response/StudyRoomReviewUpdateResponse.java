package com.jj.swm.domain.studyroom.dto.response;

import com.jj.swm.domain.studyroom.entity.StudyRoomReview;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StudyRoomReviewUpdateResponse {

    private Long studyRoomReviewId;
    private int rating;
    private double averageRating;

    public static StudyRoomReviewUpdateResponse of(StudyRoomReview studyRoomReview, double averageRating){
        return StudyRoomReviewUpdateResponse.builder()
                .studyRoomReviewId(studyRoomReview.getId())
                .rating(studyRoomReview.getRating())
                .averageRating(averageRating)
                .build();
    }
}
