package com.jj.swm.domain.study.core.dto.response;

import com.jj.swm.domain.study.core.entity.StudyRecruitmentPosition;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class GetRecruitmentPositionDetailsResponse extends GetRecruitmentPositionResponse {

    private long acceptedCount;

    private long participatedCount;

    public static GetRecruitmentPositionDetailsResponse of(
            StudyRecruitmentPosition recruitmentPosition,
            long acceptedCount,
            long participatedCount
    ) {
        return GetRecruitmentPositionDetailsResponse.builder()
                .recruitmentPositionId(recruitmentPosition.getId())
                .title(recruitmentPosition.getTitle())
                .headcount(recruitmentPosition.getHeadcount())
                .acceptedCount(acceptedCount)
                .participatedCount(participatedCount)
                .build();
    }
}
