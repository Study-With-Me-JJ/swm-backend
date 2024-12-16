package com.jj.swm.domain.studyroom.dto.response;

import com.jj.swm.domain.studyroom.entity.StudyRoomReviewReply;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateStudyRoomReviewReplyResponse {

    private Long studyRoomReviewId;
    private Long studyRoomReviewReplyId;

    public static CreateStudyRoomReviewReplyResponse from(StudyRoomReviewReply studyRoomReviewReply) {
        return CreateStudyRoomReviewReplyResponse.builder()
                .studyRoomReviewId(studyRoomReviewReply.getStudyRoomReview().getId())
                .studyRoomReviewReplyId(studyRoomReviewReply.getId())
                .build();
    }
}
