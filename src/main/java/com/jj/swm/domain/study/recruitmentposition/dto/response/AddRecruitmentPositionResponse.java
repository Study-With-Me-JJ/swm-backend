package com.jj.swm.domain.study.recruitmentposition.dto.response;

import com.jj.swm.domain.study.recruitmentposition.entity.StudyRecruitmentPosition;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AddRecruitmentPositionResponse {

    private Long recruitmentPositionId;

    public static AddRecruitmentPositionResponse from(StudyRecruitmentPosition recruitmentPosition) {
        return AddRecruitmentPositionResponse.builder()
                .recruitmentPositionId(recruitmentPosition.getId())
                .build();
    }
}
