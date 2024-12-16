package com.jj.swm.domain.studyroom.dto.request.update.type;

import com.jj.swm.domain.studyroom.entity.StudyRoomType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateStudyRoomTypeRequest {

    @NotNull
    @Positive
    private Long typeId;

    @NotNull
    private StudyRoomType type;
}
