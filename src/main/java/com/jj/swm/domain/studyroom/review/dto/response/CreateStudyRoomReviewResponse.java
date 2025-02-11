package com.jj.swm.domain.studyroom.review.dto.response;

import com.jj.swm.domain.studyroom.review.entity.StudyRoomReview;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CreateStudyRoomReviewResponse {

    private Long studyRoomReviewId;
    private int rating;
    private double averageRating;
    private List<String> imageUrls;

    public static CreateStudyRoomReviewResponse of(StudyRoomReview studyRoomReview, List<String> imageUrls) {
        return CreateStudyRoomReviewResponse.builder()
                .studyRoomReviewId(studyRoomReview.getId())
                .rating(studyRoomReview.getRating())
                .averageRating(studyRoomReview.getStudyRoom().getAverageRating())
                .imageUrls(imageUrls)
                .build();
    }
}
