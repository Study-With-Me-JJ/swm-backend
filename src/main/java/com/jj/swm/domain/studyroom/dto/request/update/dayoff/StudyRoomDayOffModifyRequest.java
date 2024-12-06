package com.jj.swm.domain.studyroom.dto.request.update.dayoff;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.DayOfWeek;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyRoomDayOffModifyRequest {

    @NotNull
    @Positive
    private Long studyRoomId;

    private List<DayOfWeek> dayOffsToAdd;
    private List<StudyRoomDayOffUpdateRequest> dayOffsToUpdate;
    private List<Long> dayOffIdsToRemove;
}
