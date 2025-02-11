package com.jj.swm.domain.studyroom.core.dto.response;

import com.jj.swm.domain.studyroom.core.entity.StudyRoomOption;
import com.jj.swm.domain.studyroom.core.entity.StudyRoomOptionInfo;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetStudyRoomOptionInfoResponse {

    private Long optionInfoId;
    private StudyRoomOption option;

    public static GetStudyRoomOptionInfoResponse from(StudyRoomOptionInfo studyRoomOptionInfo) {
        return GetStudyRoomOptionInfoResponse.builder()
                .optionInfoId(studyRoomOptionInfo.getId())
                .option(studyRoomOptionInfo.getOption())
                .build();
    }
}
