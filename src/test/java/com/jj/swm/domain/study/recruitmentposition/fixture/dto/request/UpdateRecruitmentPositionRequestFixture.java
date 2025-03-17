package com.jj.swm.domain.study.recruitmentposition.fixture.dto.request;

import com.jj.swm.domain.study.recruitmentposition.dto.request.UpdateRecruitmentPositionRequest;
import com.jj.swm.domain.study.recruitmentposition.entity.RecruitmentPositionTitle;

public class UpdateRecruitmentPositionRequestFixture {

    public static UpdateRecruitmentPositionRequest create() {
        return UpdateRecruitmentPositionRequest.builder()
                .title(RecruitmentPositionTitle.FRONTEND)
                .headcount(5)
                .acceptedCount(1)
                .build();
    }

    public static UpdateRecruitmentPositionRequest createForAcceptedCountMoreThanHeadcountFail() {
        return UpdateRecruitmentPositionRequest.builder()
                .title(RecruitmentPositionTitle.FRONTEND)
                .headcount(1)
                .acceptedCount(2)
                .build();
    }
}
