package com.jj.swm.domain.studyroom.dto.request.update.option;

import com.jj.swm.domain.studyroom.entity.StudyRoomOption;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyRoomOptionUpdateRequest {

    @NotNull
    @Positive
    private Long optionId;

    @NotNull
    private StudyRoomOption option;
}
