package com.jj.swm.domain.studyroom.dto.request.update.tag;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateStudyRoomTagRequest {

    @NotNull
    @Positive
    private Long tagId;

    @NotBlank
    private String tag;
}
