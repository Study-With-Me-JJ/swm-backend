package com.jj.swm.domain.study.core.dto.response;

import com.jj.swm.domain.study.core.entity.StudyImage;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FindStudyImageResponse {

    private Long imageId;

    private String imageUrl;

    public static FindStudyImageResponse from(StudyImage image) {
        return FindStudyImageResponse.builder()
                .imageId(image.getId())
                .imageUrl(image.getImageUrl())
                .build();
    }
}
