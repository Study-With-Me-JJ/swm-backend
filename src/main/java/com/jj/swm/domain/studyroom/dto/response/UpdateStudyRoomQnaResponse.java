package com.jj.swm.domain.studyroom.dto.response;

import com.jj.swm.domain.studyroom.entity.StudyRoomQna;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UpdateStudyRoomQnaResponse {

    private LocalDateTime updateAt;

    public static UpdateStudyRoomQnaResponse from(StudyRoomQna studyRoomQna) {
        return UpdateStudyRoomQnaResponse.builder()
                .updateAt(studyRoomQna.getUpdatedAt())
                .build();
    }
}
