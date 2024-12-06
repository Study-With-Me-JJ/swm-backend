package com.jj.swm.domain.studyroom.dto.request.update.image;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyRoomImageUpdateRequest {

    @NotNull
    @Positive
    private Long imageId;

    @NotBlank
    private String imageUrl;
}
