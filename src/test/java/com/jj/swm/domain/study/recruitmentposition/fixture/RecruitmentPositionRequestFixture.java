package com.jj.swm.domain.study.recruitmentposition.fixture;

import com.jj.swm.domain.study.recruitmentposition.dto.request.CreateRecruitmentPositionRequest;
import com.jj.swm.domain.study.recruitmentposition.dto.request.UpdateRecruitmentPositionRequest;
import com.jj.swm.domain.study.recruitmentposition.entity.RecruitmentPositionTitle;

public class RecruitmentPositionRequestFixture {

    public static CreateRecruitmentPositionRequest createRecruitmentPositionRequest() {
        return CreateRecruitmentPositionRequest.builder()
                .title(RecruitmentPositionTitle.BACKEND)
                .headcount(3)
                .build();
    }

    public static UpdateRecruitmentPositionRequest updateRecruitmentPositionRequest() {
        return UpdateRecruitmentPositionRequest.builder()
                .title(RecruitmentPositionTitle.FRONTEND)
                .headcount(5)
                .acceptedCount(1)
                .build();
    }

    public static UpdateRecruitmentPositionRequest updateRecruitmentPositionRequestAcceptedMoreThanHeadcount() {
        return UpdateRecruitmentPositionRequest.builder()
                .title(RecruitmentPositionTitle.FRONTEND)
                .headcount(1)
                .acceptedCount(2)
                .build();
    }
}
