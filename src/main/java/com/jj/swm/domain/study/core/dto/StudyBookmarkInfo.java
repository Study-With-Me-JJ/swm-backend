package com.jj.swm.domain.study.core.dto;

import com.querydsl.core.annotations.QueryProjection;

public record StudyBookmarkInfo(Long id, Long studyId) {

    @QueryProjection
    public StudyBookmarkInfo {
    }
}
