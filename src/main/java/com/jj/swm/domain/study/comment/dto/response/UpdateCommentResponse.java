package com.jj.swm.domain.study.comment.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jj.swm.domain.study.comment.entity.StudyComment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UpdateCommentResponse {

    @JsonFormat(pattern = "yy.MM.dd HH:mm")
    private LocalDateTime updatedAt;

    public static UpdateCommentResponse from(StudyComment comment) {
        return UpdateCommentResponse.builder()
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}
