package com.jj.swm.domain.studyroom.dto.response;

import com.jj.swm.domain.studyroom.entity.StudyRoomLike;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StudyRoomLikeCreateResponse {

    private Long studyRoomLikeId;
    private int studyRoomLikeCount;

    public static StudyRoomLikeCreateResponse from(StudyRoomLike studyRoomLike) {
        return StudyRoomLikeCreateResponse.builder()
                .studyRoomLikeId(studyRoomLike.getId())
                .studyRoomLikeCount(studyRoomLike.getStudyRoom().getLikeCount())
                .build();
    }
}
