package com.jj.swm.domain.study.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class StudyBookmarkInfo {

    private final Long id;

    private final Long studyId;

    @QueryProjection
    public StudyBookmarkInfo(Long id, Long studyId) {
        this.id = id;
        this.studyId = studyId;
    }
}
