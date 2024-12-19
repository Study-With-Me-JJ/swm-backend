package com.jj.swm.domain.studyroom.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateStudyRoomReviewRequest {

    @NotBlank
    private String comment;

    @Min(0)
    @Max(5)
    @NotNull
    private Integer rating;

    @Size(max = 3)
    private List<String> imageUrls;
}
