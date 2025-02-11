package com.jj.swm.domain.studyroom.dto.response;

import com.jj.swm.domain.studyroom.entity.StudyRoomImage;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetStudyRoomImageResponse {

    private Long imageId;
    private String imageUrl;
    private int sortOrder;

    public static GetStudyRoomImageResponse from(StudyRoomImage studyRoomImage) {
        return GetStudyRoomImageResponse.builder()
                .imageId(studyRoomImage.getId())
                .imageUrl(studyRoomImage.getImageUrl())
                .sortOrder(studyRoomImage.getSortOrder())
                .build();
    }
}
