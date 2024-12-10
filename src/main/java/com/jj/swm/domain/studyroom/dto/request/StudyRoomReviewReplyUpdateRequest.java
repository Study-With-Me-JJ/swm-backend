package com.jj.swm.domain.studyroom.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyRoomReviewReplyUpdateRequest {

    @NotNull
    @Positive
    private Long studyRoomReviewReplyId;

    @NotBlank
    private String reply;
}
