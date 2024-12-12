package com.jj.swm.domain.studyroom.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyRoomReviewCreateRequest {

    @NotBlank
    private String comment;

    @Min(0)
    @Max(5)
    @NotNull
    private Integer rating;

}
