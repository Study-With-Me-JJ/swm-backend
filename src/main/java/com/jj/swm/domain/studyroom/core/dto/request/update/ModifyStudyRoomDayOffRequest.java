package com.jj.swm.domain.studyroom.core.dto.request.update;

import lombok.*;

import java.time.DayOfWeek;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ModifyStudyRoomDayOffRequest {

    private List<DayOfWeek> dayOffsToAdd;

    private List<Long> dayOffIdsToRemove;
}
