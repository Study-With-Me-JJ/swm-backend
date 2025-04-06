package com.jj.swm.domain.study.core.fixture.dto.request;

import com.jj.swm.domain.study.core.dto.request.ModifyRecruitmentPositionRequest;
import com.jj.swm.domain.study.core.dto.request.UpdateRecruitmentPositionRequest;
import com.jj.swm.domain.study.core.entity.RecruitmentPositionTitle;

import java.util.List;

public class ModifyRecruitmentPositionRequestFixture {

    public static ModifyRecruitmentPositionRequest create() {
        return ModifyRecruitmentPositionRequest.builder()
                .createRecruitmentPositionRequests(List.of(CreateRecruitmentPositionRequestFixture.create()))
                .updateRecruitmentPositionRequests(List.of(UpdateRecruitmentPositionRequest.builder()
                        .recruitmentPositionId(2L)
                        .title(RecruitmentPositionTitle.ETC)
                        .headcount(2)
                        .build()))
                .recruitmentPositionIdsToRemove(List.of(1L))
                .build();
    }
}
