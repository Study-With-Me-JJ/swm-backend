package com.jj.swm.domain.studyroom.core.dto;

import com.querydsl.core.annotations.QueryProjection;

public record StudyRoomBookmarkInfo(Long id, Long studyRoomId) {

    @QueryProjection
    public StudyRoomBookmarkInfo {
    }
}
