package com.jj.swm.domain.study.core.fixture.dto.request;

import com.jj.swm.domain.study.core.dto.request.ModifyRecruitmentPositionRequest;
import com.jj.swm.domain.study.core.dto.request.UpdateRecruitmentPositionRequest;

import java.util.List;

import static com.jj.swm.domain.study.core.entity.RecruitmentPositionTitle.ETC;

public class ModifyRecruitmentPositionRequestFixture {

    public static ModifyRecruitmentPositionRequest create() {
        return ModifyRecruitmentPositionRequest.builder()
                .createRecruitmentPositionRequests(List.of(
                        CreateRecruitmentPositionRequestFixture.create(),
                        CreateRecruitmentPositionRequestFixture.create()
                )).updateRecruitmentPositionRequests(List.of(
                        UpdateRecruitmentPositionRequest.builder()
                                .recruitmentPositionId(3L)
                                .title(ETC)
                                .headcount(1)
                                .build(),
                        UpdateRecruitmentPositionRequest.builder()
                                .recruitmentPositionId(4L)
                                .title(ETC)
                                .headcount(1)
                                .build()
                )).recruitmentPositionIdsToRemove(List.of(1L, 2L))
                .build();
    }

    public static ModifyRecruitmentPositionRequest createForCreateRecruitmentPositionRequestsNullSuccess() {
        return ModifyRecruitmentPositionRequest.builder()
                .updateRecruitmentPositionRequests(List.of(
                        UpdateRecruitmentPositionRequest.builder()
                                .recruitmentPositionId(3L)
                                .title(ETC)
                                .headcount(1)
                                .build(),
                        UpdateRecruitmentPositionRequest.builder()
                                .recruitmentPositionId(4L)
                                .title(ETC)
                                .headcount(1)
                                .build()
                )).recruitmentPositionIdsToRemove(List.of(1L, 2L))
                .build();
    }

    public static ModifyRecruitmentPositionRequest createForUpdateRecruitmentPositionRequestsNullSuccess() {
        return ModifyRecruitmentPositionRequest.builder()
                .createRecruitmentPositionRequests(List.of(
                        CreateRecruitmentPositionRequestFixture.create(),
                        CreateRecruitmentPositionRequestFixture.create()
                )).recruitmentPositionIdsToRemove(List.of(1L, 2L))
                .build();
    }

    public static ModifyRecruitmentPositionRequest createForNotEqualsUpdateSizeFail() {
        return ModifyRecruitmentPositionRequest.builder()
                .updateRecruitmentPositionRequests(List.of(
                        UpdateRecruitmentPositionRequest.builder()
                                .recruitmentPositionId(5L)
                                .title(ETC)
                                .headcount(1)
                                .build()
                )).build();
    }

    public static ModifyRecruitmentPositionRequest createForHeadcountLessThenAcceptedCountFail(Long updateRecruitmentPositionId) {
        return ModifyRecruitmentPositionRequest.builder()
                .updateRecruitmentPositionRequests(List.of(
                        UpdateRecruitmentPositionRequest.builder()
                                .recruitmentPositionId(updateRecruitmentPositionId)
                                .title(ETC)
                                .headcount(1)
                                .build()
                )).build();
    }

    public static ModifyRecruitmentPositionRequest createForNotEqualsDeleteSizeFail() {
        return ModifyRecruitmentPositionRequest.builder()
                .recruitmentPositionIdsToRemove(List.of(1L, 5L))
                .build();
    }

    public static ModifyRecruitmentPositionRequest createForExceedSizeFail() {
        return ModifyRecruitmentPositionRequest.builder()
                .createRecruitmentPositionRequests(List.of(
                        CreateRecruitmentPositionRequestFixture.create(),
                        CreateRecruitmentPositionRequestFixture.create(),
                        CreateRecruitmentPositionRequestFixture.create(),
                        CreateRecruitmentPositionRequestFixture.create(),
                        CreateRecruitmentPositionRequestFixture.create(),
                        CreateRecruitmentPositionRequestFixture.create(),
                        CreateRecruitmentPositionRequestFixture.create()
                ))
                .build();
    }

    public static ModifyRecruitmentPositionRequest createForUnderSizeFail() {
        return ModifyRecruitmentPositionRequest.builder()
                .recruitmentPositionIdsToRemove(List.of(1L, 2L, 3L, 4L))
                .build();
    }
}
