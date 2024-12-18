package com.jj.swm.domain.studyroom.dto.response;

import com.jj.swm.domain.studyroom.entity.StudyRoomDayOff;
import lombok.Builder;
import lombok.Getter;

import java.time.DayOfWeek;

@Getter
@Builder
public class GetStudyRoomDayOffResponse {

    private Long dayOffId;
    private DayOfWeek dayOff;

    public static GetStudyRoomDayOffResponse from(StudyRoomDayOff studyRoomDayOff) {
        return GetStudyRoomDayOffResponse.builder()
                .dayOffId(studyRoomDayOff.getId())
                .dayOff(studyRoomDayOff.getDayOfWeek())
                .build();
    }
}
