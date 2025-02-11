package com.jj.swm.domain.studyroom.core.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeleteStudyRoomRequest {

    @NotNull
    @Positive
    private Long studyRoomId;
}
