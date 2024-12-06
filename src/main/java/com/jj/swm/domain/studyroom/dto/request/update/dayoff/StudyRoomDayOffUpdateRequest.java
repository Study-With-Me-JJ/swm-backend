package com.jj.swm.domain.studyroom.dto.request.update.dayoff;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.DayOfWeek;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyRoomDayOffUpdateRequest {

    @NotNull
    @Positive
    private Long dayOffId;

    @NotNull
    private DayOfWeek dayOff;
}
