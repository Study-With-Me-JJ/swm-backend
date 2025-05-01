package com.jj.swm.domain.studyroom.core.dto.response;

import com.jj.swm.domain.studyroom.core.entity.StudyRoomTag;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetStudyRoomTagResponse {

    private Long studyRoomTagId;
    private String tag;

    public static GetStudyRoomTagResponse from(StudyRoomTag studyRoomTag) {
        return GetStudyRoomTagResponse.builder()
                .studyRoomTagId(studyRoomTag.getId())
                .tag(studyRoomTag.getTag())
                .build();
    }
}
