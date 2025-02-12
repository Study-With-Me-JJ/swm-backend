package com.jj.swm.domain.studyroom.core.dto.response;

import com.jj.swm.domain.studyroom.core.entity.StudyRoomImage;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetStudyRoomImageResponse {

    private Long imageId;
    private String imageUrl;

    public static GetStudyRoomImageResponse from(StudyRoomImage studyRoomImage) {
        return GetStudyRoomImageResponse.builder()
                .imageId(studyRoomImage.getId())
                .imageUrl(studyRoomImage.getImageUrl())
                .build();
    }
}
