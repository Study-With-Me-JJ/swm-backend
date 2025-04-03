package com.jj.swm.domain.study.recruitmentposition.fixture.dto.request;

import com.jj.swm.domain.study.recruitmentposition.dto.request.UpsertRecruitmentPositionRequest;
import com.jj.swm.domain.study.recruitmentposition.entity.RecruitmentPositionTitle;

public class UpsertRecruitmentPositionRequestFixture {

    public static UpsertRecruitmentPositionRequest create() {
        return UpsertRecruitmentPositionRequest.builder()
                .title(RecruitmentPositionTitle.BACKEND)
                .headcount(3)
                .build();
    }

    public static UpsertRecruitmentPositionRequest update() {
        return UpsertRecruitmentPositionRequest.builder()
                .title(RecruitmentPositionTitle.FRONTEND)
                .headcount(5)
                .build();
    }

    public static UpsertRecruitmentPositionRequest updateForAcceptedCountLessThanHeadcountFail() {
        return UpsertRecruitmentPositionRequest.builder()
                .title(RecruitmentPositionTitle.FRONTEND)
                .headcount(1)
                .build();
    }
}
