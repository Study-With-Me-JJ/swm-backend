package com.jj.swm.domain.study.core.fixture.dto.request;

import com.jj.swm.domain.study.core.dto.component.CreateRecruitmentPositionInfo;

import static com.jj.swm.domain.study.core.entity.StudyRecruitmentPosition.RecruitmentPositionTitle.BACKEND;

public class CreateRecruitmentPositionInfoFixture {

    public static CreateRecruitmentPositionInfo create() {
        return CreateRecruitmentPositionInfo.builder()
                .title(BACKEND)
                .headcount(3)
                .build();
    }
}
