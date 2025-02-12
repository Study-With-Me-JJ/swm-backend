package com.jj.swm.domain.study.core.dto.response;

import com.jj.swm.domain.study.core.entity.StudyBookmark;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AddStudyBookmarkResponse {

    private Long bookmarkId;

    public static AddStudyBookmarkResponse from(StudyBookmark studyBookmark) {
        return AddStudyBookmarkResponse.builder()
                .bookmarkId(studyBookmark.getId())
                .build();
    }
}
