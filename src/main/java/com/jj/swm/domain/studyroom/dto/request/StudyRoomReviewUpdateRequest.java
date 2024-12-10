package com.jj.swm.domain.studyroom.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import org.checkerframework.checker.units.qual.A;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyRoomReviewUpdateRequest {

    @NotNull
    @Positive
    private Long studyRoomReviewId;

    @NotNull
    @Positive
    private Long studyRoomId;

    @NotBlank
    private String comment;

    @Min(0)
    @Max(5)
    @NotNull
    private Integer rating;
}
