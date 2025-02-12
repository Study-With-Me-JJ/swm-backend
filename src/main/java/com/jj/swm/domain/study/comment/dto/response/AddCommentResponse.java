package com.jj.swm.domain.study.comment.dto.response;

import com.jj.swm.domain.study.comment.entity.StudyComment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AddCommentResponse {

    private Long commentId;

    private LocalDateTime createdAt;

    public static AddCommentResponse from(StudyComment comment) {
        return AddCommentResponse.builder()
                .commentId(comment.getId())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
