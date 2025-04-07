package com.jj.swm.domain.study.core.dto.response;

import com.jj.swm.domain.study.core.entity.StudyRecruitmentPosition;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class GetRecruitmentPositionDetailsResponse extends GetRecruitmentPositionResponse {

    private Integer acceptedCount;

    public static GetRecruitmentPositionDetailsResponse of(
            StudyRecruitmentPosition recruitmentPosition, Integer acceptedCount
    ) {
        return GetRecruitmentPositionDetailsResponse.builder()
                .recruitmentPositionId(recruitmentPosition.getId())
                .title(recruitmentPosition.getTitle())
                .headcount(recruitmentPosition.getHeadcount())
                .acceptedCount(acceptedCount)
                .build();
    }
}
