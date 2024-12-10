package com.jj.swm.domain.study.dto.response;

import com.jj.swm.domain.study.entity.StudyComment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CommentUpdateResponse {

    private LocalDateTime updatedAt;

    public static CommentUpdateResponse from(StudyComment studyComment) {
        return CommentUpdateResponse.builder()
                .updatedAt(studyComment.getUpdatedAt())
                .build();
    }
}
