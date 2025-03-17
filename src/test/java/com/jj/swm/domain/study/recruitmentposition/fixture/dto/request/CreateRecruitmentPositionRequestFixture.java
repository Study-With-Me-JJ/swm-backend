package com.jj.swm.domain.study.recruitmentposition.fixture.dto.request;

import com.jj.swm.domain.study.recruitmentposition.dto.request.CreateRecruitmentPositionRequest;
import com.jj.swm.domain.study.recruitmentposition.entity.RecruitmentPositionTitle;

public class CreateRecruitmentPositionRequestFixture {

    public static CreateRecruitmentPositionRequest create() {
        return CreateRecruitmentPositionRequest.builder()
                .title(RecruitmentPositionTitle.BACKEND)
                .headcount(3)
                .build();
    }
}
