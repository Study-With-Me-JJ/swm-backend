package com.jj.swm.domain.studyroom.qna.dto.response;

import com.jj.swm.domain.studyroom.qna.entity.StudyRoomQna;
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
