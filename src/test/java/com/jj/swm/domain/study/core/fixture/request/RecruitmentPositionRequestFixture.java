package com.jj.swm.domain.study.core.fixture.request;

import com.jj.swm.domain.study.recruitmentposition.dto.request.CreateRecruitmentPositionRequest;
import com.jj.swm.domain.study.recruitmentposition.entity.RecruitmentPositionTitle;

public class RecruitmentPositionRequestFixture {

    public static CreateRecruitmentPositionRequest buildCreateRecruitmentPositionRequest() {
        return CreateRecruitmentPositionRequest.builder()
                .title(RecruitmentPositionTitle.BACKEND)
                .headcount(3)
                .build();
    }
}
