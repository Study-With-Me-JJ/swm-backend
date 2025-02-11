package com.jj.swm.domain.study.comment.dto.response;

import com.jj.swm.domain.study.comment.entity.StudyComment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CommentUpdateResponse {

    private LocalDateTime updatedAt;

    public static CommentUpdateResponse from(StudyComment comment) {
        return CommentUpdateResponse.builder()
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}
