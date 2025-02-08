package com.jj.swm.domain.study.dto.comment.response;

import com.jj.swm.domain.study.entity.comment.StudyComment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class ReplyInquiryResponse {

    private Long replyId;

    private String content;

    private UUID userId;

    private String nickname;

    private String profileImageUrl;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public static ReplyInquiryResponse from(StudyComment comment) {
        return ReplyInquiryResponse.builder()
                .replyId(comment.getId())
                .content(comment.getContent())
                .userId(comment.getUser().getId())
                .nickname(comment.getUser().getNickname())
                .profileImageUrl(comment.getUser().getProfileImageUrl())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}
