package com.jj.swm.domain.study.dto.comment.response;

import com.jj.swm.domain.study.entity.comment.StudyComment;
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
