package com.jj.swm.domain.study.comment.dto.response;

import com.jj.swm.domain.study.comment.entity.StudyComment;
import com.jj.swm.domain.user.core.dto.component.UserInfo;
import com.jj.swm.global.common.dto.component.BaseTimeInfo;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetStudyParentCommentResponse {

    private Long commentId;

    private String content;

    private UserInfo userInfo;

    private BaseTimeInfo baseTimeInfo;

    private Long replyCount;

    public static GetStudyParentCommentResponse of(StudyComment parent, Long childrenCount) {
        return GetStudyParentCommentResponse.builder()
                .commentId(parent.getId())
                .content(parent.getContent())
                .userInfo(UserInfo.from(parent.getUser()))
                .baseTimeInfo(BaseTimeInfo.of(parent.getCreatedAt(), parent.getUpdatedAt()))
                .replyCount(childrenCount)
                .build();
    }
}
