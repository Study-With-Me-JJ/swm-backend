package com.jj.swm.domain.study.core.fixture.dto.request;

import com.jj.swm.domain.study.core.dto.request.CreateRecruitmentPositionRequest;
import com.jj.swm.domain.study.core.entity.RecruitmentPositionTitle;

public class CreateRecruitmentPositionRequestFixture {

    public static CreateRecruitmentPositionRequest create() {
        return CreateRecruitmentPositionRequest.builder()
                .title(RecruitmentPositionTitle.BACKEND)
                .headcount(3)
                .build();
    }
}
