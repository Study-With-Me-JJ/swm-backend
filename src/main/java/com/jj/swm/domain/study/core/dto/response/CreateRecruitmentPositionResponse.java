package com.jj.swm.domain.study.core.dto.response;

import com.jj.swm.domain.study.core.entity.StudyRecruitmentPosition;
import com.jj.swm.domain.study.core.entity.StudyRecruitmentPosition.RecruitmentPositionTitle;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateRecruitmentPositionResponse {

    private Long recruitmentPositionId;

    private RecruitmentPositionTitle title;

    private Integer headcount;

    public static CreateRecruitmentPositionResponse from(StudyRecruitmentPosition recruitmentPosition) {
        return CreateRecruitmentPositionResponse.builder()
                .recruitmentPositionId(recruitmentPosition.getId())
                .title(recruitmentPosition.getTitle())
                .headcount(recruitmentPosition.getHeadcount())
                .build();
    }
}
