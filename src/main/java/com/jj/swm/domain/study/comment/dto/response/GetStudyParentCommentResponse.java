package com.jj.swm.domain.study.comment.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jj.swm.domain.study.comment.entity.StudyComment;
import com.jj.swm.domain.user.core.dto.response.UserInfoResponse;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class GetStudyParentCommentResponse {

    private Long commentId;

    private String content;

    private UserInfoResponse userInfo;

    @JsonFormat(pattern = "yy.MM.dd HH:mm")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yy.MM.dd HH:mm")
    private LocalDateTime updatedAt;

    private Long replyCount;

    public static GetStudyParentCommentResponse of(StudyComment parent, Long childrenCount) {
        return GetStudyParentCommentResponse.builder()
                .commentId(parent.getId())
                .content(parent.getContent())
                .userInfo(UserInfoResponse.from(parent.getUser()))
                .createdAt(parent.getCreatedAt())
                .updatedAt(parent.getUpdatedAt())
                .replyCount(childrenCount)
                .build();
    }
}
