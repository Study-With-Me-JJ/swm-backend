package com.jj.swm.domain.study.core.fixture.dto.request;

import com.jj.swm.domain.study.core.dto.request.CreateRecruitmentPositionRequest;

import static com.jj.swm.domain.study.core.entity.RecruitmentPositionTitle.BACKEND;

public class CreateRecruitmentPositionRequestFixture {

    public static CreateRecruitmentPositionRequest create() {
        return CreateRecruitmentPositionRequest.builder()
                .title(BACKEND)
                .headcount(3)
                .build();
    }
}
