package com.jj.swm.domain.study.dto.response;

import com.jj.swm.domain.study.entity.StudyRecruitmentPosition;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecruitmentPositionCreateResponse {

    private Long recruitmentPositionId;

    public static RecruitmentPositionCreateResponse from(StudyRecruitmentPosition recruitmentPosition) {
        return RecruitmentPositionCreateResponse.builder()
                .recruitmentPositionId(recruitmentPosition.getId())
                .build();
    }
}
