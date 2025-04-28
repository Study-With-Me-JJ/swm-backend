package com.jj.swm.domain.study.core.dto.response;

import com.jj.swm.domain.study.core.entity.RecruitmentPositionTitle;
import com.jj.swm.domain.study.core.entity.StudyRecruitmentPosition;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetRecruitmentPositionDetailsResponse {

    private Long recruitmentPositionId;

    private RecruitmentPositionTitle title;

    private Integer headcount;

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
