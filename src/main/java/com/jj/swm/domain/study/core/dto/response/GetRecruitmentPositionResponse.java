package com.jj.swm.domain.study.core.dto.response;

import com.jj.swm.domain.study.core.entity.RecruitmentPositionTitle;
import com.jj.swm.domain.study.core.entity.StudyRecruitmentPosition;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
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
