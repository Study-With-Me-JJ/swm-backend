package com.jj.swm.domain.studyroom.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyRoomReviewReplyCreateRequest {

    @NotNull
    @Positive
    private Long studyRoomReviewId;

    @NotNull
    @Positive
    private Long studyRoomId;

    @NotBlank
    private String reply;
}
