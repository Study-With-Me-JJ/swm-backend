package com.jj.swm.domain.studyroom.dto.response;

import com.jj.swm.domain.studyroom.entity.StudyRoomBookmark;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateStudyRoomBookmarkResponse {

    private Long bookmarkId;

    public static CreateStudyRoomBookmarkResponse from(StudyRoomBookmark studyRoomBookmark){
        return CreateStudyRoomBookmarkResponse.builder()
                .bookmarkId(studyRoomBookmark.getId())
                .build();
    }
}
