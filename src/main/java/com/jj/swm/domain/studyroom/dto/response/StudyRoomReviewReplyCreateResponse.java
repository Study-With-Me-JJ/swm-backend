package com.jj.swm.domain.studyroom.dto.response;

import com.jj.swm.domain.studyroom.entity.StudyRoomReviewReply;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StudyRoomReviewReplyCreateResponse {

    private Long studyRoomReviewId;
    private Long studyRoomReviewReplyId;

    public static StudyRoomReviewReplyCreateResponse from(StudyRoomReviewReply studyRoomReviewReply) {
        return StudyRoomReviewReplyCreateResponse.builder()
                .studyRoomReviewId(studyRoomReviewReply.getStudyRoomReview().getId())
                .studyRoomReviewReplyId(studyRoomReviewReply.getId())
                .build();
    }
}
