package com.jj.swm.domain.study.comment.dto.response;

import com.jj.swm.domain.study.comment.entity.StudyComment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ModifyCommentResponse {

    private LocalDateTime updatedAt;

    public static ModifyCommentResponse from(StudyComment comment) {
        return ModifyCommentResponse.builder()
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}
