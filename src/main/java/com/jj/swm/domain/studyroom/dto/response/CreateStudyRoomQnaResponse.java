package com.jj.swm.domain.studyroom.dto.response;

import com.jj.swm.domain.studyroom.entity.StudyRoomQna;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateStudyRoomQnaResponse {

    private Long studyRoomQnaId;

    public static CreateStudyRoomQnaResponse from(StudyRoomQna studyRoomQna) {
        return CreateStudyRoomQnaResponse.builder()
                .studyRoomQnaId(studyRoomQna.getId())
                .build();
    }
}
