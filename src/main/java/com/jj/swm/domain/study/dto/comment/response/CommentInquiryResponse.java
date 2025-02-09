package com.jj.swm.domain.study.dto.comment.response;

import com.jj.swm.domain.study.entity.comment.StudyComment;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@SuperBuilder
public class CommentInquiryResponse {

    private Long commentId;

    private String content;

    private UUID userId;

    private String nickname;

    private String profileImageUrl;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public static CommentInquiryResponse from(StudyComment comment) {
        return CommentInquiryResponse.builder()
                .commentId(comment.getId())
                .content(comment.getContent())
                .userId(comment.getUser().getId())
                .nickname(comment.getUser().getNickname())
                .profileImageUrl(comment.getUser().getProfileImageUrl())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}
