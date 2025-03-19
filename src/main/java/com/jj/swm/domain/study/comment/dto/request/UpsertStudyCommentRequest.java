package com.jj.swm.domain.study.comment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpsertStudyCommentRequest {

    @NotBlank
    @Size(max = 255)
    private String content;
}
