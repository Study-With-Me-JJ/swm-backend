package com.jj.swm.domain.study.comment.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jj.swm.domain.study.comment.entity.StudyComment;
import com.jj.swm.domain.user.core.dto.response.UserInfoResponse;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class GetStudyReplyResponse {

    private Long commentId;

    private String content;

    private UserInfoResponse userInfo;

    @JsonFormat(pattern = "yy.MM.dd HH:mm")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yy.MM.dd HH:mm")
    private LocalDateTime updatedAt;

    public static GetStudyReplyResponse from(StudyComment comment) {
        return GetStudyReplyResponse.builder()
                .commentId(comment.getId())
                .content(comment.getContent())
                .userInfo(UserInfoResponse.from(comment.getUser()))
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}
