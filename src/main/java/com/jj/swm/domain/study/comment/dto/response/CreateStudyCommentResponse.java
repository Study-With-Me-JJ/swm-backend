package com.jj.swm.domain.study.comment.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jj.swm.domain.study.comment.entity.StudyComment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CreateStudyCommentResponse {

    private Long commentId;

    @JsonFormat(pattern = "yy.MM.dd HH:mm")
    private LocalDateTime createdAt;

    public static CreateStudyCommentResponse from(StudyComment comment) {
        return CreateStudyCommentResponse.builder()
                .commentId(comment.getId())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
