package com.jj.swm.domain.studyroom.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class StudyRoomBookmarkInfo {

    private final Long id;

    private final Long studyRoomId;

    @QueryProjection
    public StudyRoomBookmarkInfo(Long id, Long studyRoomId) {
        this.id = id;
        this.studyRoomId = studyRoomId;
    }
}
