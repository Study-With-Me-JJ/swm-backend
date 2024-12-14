package com.jj.swm.domain.study.dto.response;

import com.jj.swm.domain.study.entity.StudyComment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CommentInquiryResponse {

    private Long commentId;

    private String content;

    private String nickname;

    private String profileImageUrl;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private int replyCount;

    public static CommentInquiryResponse of(StudyComment comment, int replyCount) {
        return CommentInquiryResponse.builder()
                .commentId(comment.getId())
                .content(comment.getContent())
                .nickname(comment.getUser().getNickname())
                .profileImageUrl(comment.getUser().getProfileImageUrl())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .replyCount(replyCount)
                .build();
    }
}
