package com.jj.swm.domain.studyroom.dto.request.update.dayoff;

import lombok.*;

import java.time.DayOfWeek;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ModifyStudyRoomDayOffRequest {

    private List<DayOfWeek> dayOffsToAdd;
    private List<UpdateStudyRoomDayOffRequest> dayOffsToUpdate;
    private List<Long> dayOffIdsToRemove;
}
