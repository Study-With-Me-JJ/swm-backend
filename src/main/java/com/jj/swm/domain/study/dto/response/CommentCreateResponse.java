package com.jj.swm.domain.study.dto.response;

import com.jj.swm.domain.study.entity.StudyComment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CommentCreateResponse {

    private Long commentId;

    private LocalDateTime createdAt;

    public static CommentCreateResponse from(StudyComment comment) {
        return CommentCreateResponse.builder()
                .commentId(comment.getId())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
