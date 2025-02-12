package com.jj.swm.domain.study.recruitmentposition.dto.response;

import com.jj.swm.domain.study.recruitmentposition.entity.StudyRecruitmentPosition;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FindRecruitmentPositionResponse {

    private Long recruitmentPositionId;

    private String title;

    private Integer headcount;

    private Integer acceptedCount;

    public static FindRecruitmentPositionResponse from(StudyRecruitmentPosition recruitmentPosition) {
        return FindRecruitmentPositionResponse.builder()
                .recruitmentPositionId(recruitmentPosition.getId())
                .title(recruitmentPosition.getTitle())
                .headcount(recruitmentPosition.getHeadcount())
                .acceptedCount(recruitmentPosition.getAcceptedCount())
                .build();
    }
}
