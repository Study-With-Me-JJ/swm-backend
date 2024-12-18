package com.jj.swm.domain.study.dto.response;

import com.jj.swm.domain.study.entity.StudyComment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReplyInquiryResponse {

    private Long replyId;

    private String content;

    private String nickname;

    private String profileImageUrl;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public static ReplyInquiryResponse from(StudyComment comment) {
        return ReplyInquiryResponse.builder()
                .replyId(comment.getId())
                .content(comment.getContent())
                .nickname(comment.getUser().getNickname())
                .profileImageUrl(comment.getUser().getProfileImageUrl())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}
