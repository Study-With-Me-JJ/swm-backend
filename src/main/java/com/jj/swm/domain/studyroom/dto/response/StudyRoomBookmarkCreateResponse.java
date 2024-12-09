package com.jj.swm.domain.studyroom.dto.response;

import com.jj.swm.domain.studyroom.entity.StudyRoomBookmark;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StudyRoomBookmarkCreateResponse {

    private Long bookmarkId;

    public static StudyRoomBookmarkCreateResponse from(StudyRoomBookmark studyRoomBookmark){
        return StudyRoomBookmarkCreateResponse.builder()
                .bookmarkId(studyRoomBookmark.getId())
                .build();
    }
}
