package com.jj.swm.domain.studyroom.dto.response;

import com.jj.swm.domain.studyroom.entity.StudyRoomReviewReply;
import com.jj.swm.domain.user.dto.response.UserInfoResponse;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetStudyRoomReviewReplyResponse {

    private Long studyRoomReviewReplyId;

    private UserInfoResponse userInfo;

    private String reply;

    public static GetStudyRoomReviewReplyResponse from(StudyRoomReviewReply studyRoomReviewReply) {
        return GetStudyRoomReviewReplyResponse.builder()
                .studyRoomReviewReplyId(studyRoomReviewReply.getId())
                .userInfo(UserInfoResponse.from(studyRoomReviewReply.getUser()))
                .reply(studyRoomReviewReply.getReply())
                .build();
    }
}
