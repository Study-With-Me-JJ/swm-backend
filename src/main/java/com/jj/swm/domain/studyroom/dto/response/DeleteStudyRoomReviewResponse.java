package com.jj.swm.domain.studyroom.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeleteStudyRoomReviewResponse {

    private Long studyRoomReviewId;
    private double averageRating;

    public static DeleteStudyRoomReviewResponse of(Long studyRoomReviewId, double averageRating) {
        return DeleteStudyRoomReviewResponse.builder()
                .studyRoomReviewId(studyRoomReviewId)
                .averageRating(averageRating)
                .build();
    }
}
