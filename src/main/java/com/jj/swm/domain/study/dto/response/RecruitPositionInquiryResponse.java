package com.jj.swm.domain.study.dto.response;

import com.jj.swm.domain.study.entity.StudyRecruitmentPosition;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecruitPositionInquiryResponse {

    private Long recruitPositionId;

    private String title;

    private Integer headcount;

    private Integer acceptedCount;

    public static RecruitPositionInquiryResponse from(StudyRecruitmentPosition recruitmentPosition) {
        return RecruitPositionInquiryResponse.builder()
                .recruitPositionId(recruitmentPosition.getId())
                .title(recruitmentPosition.getTitle())
                .headcount(recruitmentPosition.getHeadcount())
                .acceptedCount(recruitmentPosition.getAcceptedCount())
                .build();
    }
}
