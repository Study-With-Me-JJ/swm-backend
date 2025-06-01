package com.jj.swm.domain.study.comment.dto.response;

import com.jj.swm.domain.study.comment.entity.StudyComment;
import com.jj.swm.domain.user.core.dto.component.UserInfo;
import com.jj.swm.global.common.dto.component.BaseTimeInfo;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetStudyChildCommentResponse {

    private Long replyId;

    private String content;

    private UserInfo userInfo;

    private BaseTimeInfo baseTimeInfo;

    public static GetStudyChildCommentResponse from(StudyComment child) {
        return GetStudyChildCommentResponse.builder()
                .replyId(child.getId())
                .content(child.getContent())
                .userInfo(UserInfo.from(child.getUser()))
                .baseTimeInfo(BaseTimeInfo.of(child.getCreatedAt(), child.getUpdatedAt()))
                .build();
    }
}
