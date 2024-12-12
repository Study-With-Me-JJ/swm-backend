package com.jj.swm.domain.studyroom.dto.response;

import com.jj.swm.domain.studyroom.entity.StudyRoomQna;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StudyRoomQnaCreateResponse {

    private Long studyRoomQnaId;

    public static StudyRoomQnaCreateResponse from(StudyRoomQna studyRoomQna) {
        return StudyRoomQnaCreateResponse.builder()
                .studyRoomQnaId(studyRoomQna.getId())
                .build();
    }
}
