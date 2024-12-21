package com.jj.swm.domain.studyroom.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jj.swm.domain.studyroom.entity.StudyRoomQna;
import com.jj.swm.domain.user.dto.response.UserInfoResponse;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class GetStudyRoomQnaResponse {

    private Long studyRoomQnaId;
    private String comment;

    @JsonFormat(pattern = "yyyy.MM.dd HH:mm:ss")
    private LocalDateTime updatedAt;

    private UserInfoResponse userInfo;
    private List<GetStudyRoomQnaResponse> childQnas;

    public static GetStudyRoomQnaResponse from(StudyRoomQna qna) {
        List<GetStudyRoomQnaResponse> childQnas = null;
        if (qna.getParent() == null) {
            childQnas = qna.getChildren()
                    .stream()
                    .map(GetStudyRoomQnaResponse::from)
                    .toList();
        }

        return GetStudyRoomQnaResponse.builder()
                .studyRoomQnaId(qna.getId())
                .comment(qna.getComment())
                .updatedAt(qna.getUpdatedAt())
                .userInfo(UserInfoResponse.from(qna.getUser()))
                .childQnas(childQnas)  // 자식 Qna 목록 설정
                .build();
    }
}
