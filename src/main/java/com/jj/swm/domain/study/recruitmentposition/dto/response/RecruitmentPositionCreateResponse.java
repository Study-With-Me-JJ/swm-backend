package com.jj.swm.domain.study.recruitmentposition.dto.response;

import com.jj.swm.domain.study.recruitmentposition.entity.StudyRecruitmentPosition;
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
