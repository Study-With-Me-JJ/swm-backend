package com.jj.swm.domain.studyroom.qna.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jj.swm.domain.studyroom.qna.entity.StudyRoomQna;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UpdateStudyRoomQnaResponse {

    @JsonFormat(pattern = "yyyy.MM.dd HH:mm:ss")
    private LocalDateTime updateAt;

    public static UpdateStudyRoomQnaResponse from(StudyRoomQna studyRoomQna) {
        return UpdateStudyRoomQnaResponse.builder()
                .updateAt(studyRoomQna.getUpdatedAt())
                .build();
    }
}
