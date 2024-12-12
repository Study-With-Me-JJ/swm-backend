package com.jj.swm.domain.studyroom.dto.response;

import com.jj.swm.domain.studyroom.entity.StudyRoomQna;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class StudyRoomQnaUpdateResponse {

    private LocalDateTime updateAt;

    public static StudyRoomQnaUpdateResponse from(StudyRoomQna studyRoomQna) {
        return StudyRoomQnaUpdateResponse.builder()
                .updateAt(studyRoomQna.getUpdatedAt())
                .build();
    }
}
