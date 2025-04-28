package com.jj.swm.domain.study.comment.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jj.swm.domain.study.comment.entity.StudyComment;
import com.jj.swm.domain.user.core.dto.response.UserInfoResponse;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class GetStudyCommentResponse {

    private Long commentId;

    private String content;

    private UserInfoResponse userInfo;

    @JsonFormat(pattern = "yy.MM.dd HH:mm")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yy.MM.dd HH:mm")
    private LocalDateTime updatedAt;

    private Long replyCount;

    public static GetStudyCommentResponse of(StudyComment comment, Long replyCount) {
        return GetStudyCommentResponse.builder()
                .commentId(comment.getId())
                .content(comment.getContent())
                .userInfo(UserInfoResponse.from(comment.getUser()))
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .replyCount(replyCount)
                .build();
    }
}
