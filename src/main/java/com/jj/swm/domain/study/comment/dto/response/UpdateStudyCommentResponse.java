package com.jj.swm.domain.study.comment.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UpdateStudyCommentResponse {

    @JsonFormat(pattern = "yy.MM.dd HH:mm")
    private LocalDateTime updatedAt;

    public static UpdateStudyCommentResponse from() {
        return UpdateStudyCommentResponse.builder()
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
