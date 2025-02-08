package com.jj.swm.domain.study.dto.core.response;

import com.jj.swm.domain.study.entity.core.StudyTag;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StudyTagInquiryResponse {

    private Long studyTagId;

    private String name;

    public static StudyTagInquiryResponse from(StudyTag studyTag) {
        return StudyTagInquiryResponse.builder()
                .studyTagId(studyTag.getId())
                .name(studyTag.getName())
                .build();
    }
}
