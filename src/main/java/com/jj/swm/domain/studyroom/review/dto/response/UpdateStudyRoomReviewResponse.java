package com.jj.swm.domain.studyroom.review.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jj.swm.domain.studyroom.review.entity.StudyRoomReview;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UpdateStudyRoomReviewResponse {

    private Long studyRoomReviewId;
    private int rating;
    private double averageRating;

    @JsonFormat(pattern = "yyyy.MM.dd HH:mm:ss")
    private LocalDateTime updatedAt;

    public static UpdateStudyRoomReviewResponse of(StudyRoomReview studyRoomReview, double averageRating){
        return UpdateStudyRoomReviewResponse.builder()
                .studyRoomReviewId(studyRoomReview.getId())
                .rating(studyRoomReview.getRating())
                .updatedAt(studyRoomReview.getUpdatedAt())
                .averageRating(averageRating)
                .build();
    }
}
