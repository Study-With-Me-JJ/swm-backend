package com.jj.swm.domain.study.core.dto.response;

import com.jj.swm.domain.study.core.entity.RecruitmentPositionTitle;
import com.jj.swm.domain.study.core.entity.StudyRecruitmentPosition;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class GetRecruitmentPositionResponse {

    private Long recruitmentPositionId;

    private RecruitmentPositionTitle title;

    private Integer headcount;

    public static GetRecruitmentPositionResponse from(StudyRecruitmentPosition recruitmentPosition) {
        return GetRecruitmentPositionResponse.builder()
                .recruitmentPositionId(recruitmentPosition.getId())
                .title(recruitmentPosition.getTitle())
                .headcount(recruitmentPosition.getHeadcount())
                .build();
    }
}
