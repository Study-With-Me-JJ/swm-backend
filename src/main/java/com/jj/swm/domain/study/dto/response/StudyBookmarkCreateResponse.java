package com.jj.swm.domain.study.dto.response;

import com.jj.swm.domain.study.entity.StudyBookmark;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StudyBookmarkCreateResponse {

    private Long bookmarkId;

    public static StudyBookmarkCreateResponse from(StudyBookmark studyBookmark) {
        return StudyBookmarkCreateResponse.builder()
                .bookmarkId(studyBookmark.getId())
                .build();
    }
}
