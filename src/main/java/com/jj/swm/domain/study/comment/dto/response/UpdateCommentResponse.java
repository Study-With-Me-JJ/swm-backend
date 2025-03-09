package com.jj.swm.domain.study.comment.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UpdateCommentResponse {

    private LocalDateTime updatedAt;

    public static UpdateCommentResponse from() {
        return UpdateCommentResponse.builder()
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
