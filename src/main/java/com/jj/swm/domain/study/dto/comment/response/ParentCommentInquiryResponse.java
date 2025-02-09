package com.jj.swm.domain.study.dto.comment.response;

import com.jj.swm.domain.study.entity.comment.StudyComment;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class ParentCommentInquiryResponse extends CommentInquiryResponse {

    private int replyCount;

    public static ParentCommentInquiryResponse of(StudyComment comment, int replyCount) {
        return ParentCommentInquiryResponse.builder()
                .commentId(comment.getId())
                .content(comment.getContent())
                .userId(comment.getUser().getId())
                .nickname(comment.getUser().getNickname())
                .profileImageUrl(comment.getUser().getProfileImageUrl())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .replyCount(replyCount)
                .build();
    }
}
