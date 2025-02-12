package com.jj.swm.domain.study.dto.response;

import com.jj.swm.domain.study.entity.StudyImage;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StudyImageInquiryResponse {

    private Long imageId;

    private String imageUrl;

    public static StudyImageInquiryResponse from(StudyImage image) {
        return StudyImageInquiryResponse.builder()
                .imageId(image.getId())
                .imageUrl(image.getImageUrl())
                .build();
    }
}
