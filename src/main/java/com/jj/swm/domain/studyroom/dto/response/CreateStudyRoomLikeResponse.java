package com.jj.swm.domain.studyroom.dto.response;

import com.jj.swm.domain.studyroom.entity.StudyRoomLike;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateStudyRoomLikeResponse {

    private Long studyRoomLikeId;
    private int studyRoomLikeCount;

    public static CreateStudyRoomLikeResponse from(StudyRoomLike studyRoomLike) {
        return CreateStudyRoomLikeResponse.builder()
                .studyRoomLikeId(studyRoomLike.getId())
                .studyRoomLikeCount(studyRoomLike.getStudyRoom().getLikeCount())
                .build();
    }
}
