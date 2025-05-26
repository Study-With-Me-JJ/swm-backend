package com.jj.swm.domain.study.core.fixture.dto.request;

import com.jj.swm.domain.study.core.dto.request.ModifyRecruitmentPositionRequest;
import com.jj.swm.domain.study.core.dto.request.ModifyRecruitmentPositionRequest.UpdateRecruitmentPositionInfo;

import java.util.List;

import static com.jj.swm.domain.study.core.entity.StudyRecruitmentPosition.RecruitmentPositionTitle.ETC;

public class ModifyRecruitmentPositionRequestFixture {

    public static ModifyRecruitmentPositionRequest create() {
        return ModifyRecruitmentPositionRequest.builder()
                .recruitmentPositionInfosToAdd(List.of(
                        CreateRecruitmentPositionInfoFixture.create(),
                        CreateRecruitmentPositionInfoFixture.create()
                )).recruitmentPositionInfosToEdit(List.of(
                        UpdateRecruitmentPositionInfoFixture.create(3L),
                        UpdateRecruitmentPositionInfoFixture.create(4L)
                )).recruitmentPositionIdsToRemove(List.of(1L, 2L))
                .build();
    }

    public static ModifyRecruitmentPositionRequest createForCreateRecruitmentPositionRequestsNullSuccess() {
        return ModifyRecruitmentPositionRequest.builder()
                .recruitmentPositionInfosToEdit(List.of(
                        UpdateRecruitmentPositionInfoFixture.create(3L),
                        UpdateRecruitmentPositionInfoFixture.create(4L)
                )).recruitmentPositionIdsToRemove(List.of(1L, 2L))
                .build();
    }

    public static ModifyRecruitmentPositionRequest createForUpdateRecruitmentPositionRequestsNullSuccess() {
        return ModifyRecruitmentPositionRequest.builder()
                .recruitmentPositionInfosToAdd(List.of(
                        CreateRecruitmentPositionInfoFixture.create(),
                        CreateRecruitmentPositionInfoFixture.create()
                )).recruitmentPositionIdsToRemove(List.of(1L, 2L))
                .build();
    }

    public static ModifyRecruitmentPositionRequest createForNotEqualsUpdateSizeFail() {
        return ModifyRecruitmentPositionRequest.builder()
                .recruitmentPositionInfosToEdit(List.of(
                        UpdateRecruitmentPositionInfoFixture.create(5L)
                )).build();
    }

    public static ModifyRecruitmentPositionRequest createForHeadcountLessThenAcceptedCountFail(Long updateRecruitmentPositionId) {
        return ModifyRecruitmentPositionRequest.builder()
                .recruitmentPositionInfosToEdit(List.of(
                        UpdateRecruitmentPositionInfoFixture.create(updateRecruitmentPositionId)
                )).build();
    }

    public static ModifyRecruitmentPositionRequest createForNotEqualsDeleteSizeFail() {
        return ModifyRecruitmentPositionRequest.builder()
                .recruitmentPositionIdsToRemove(List.of(1L, 5L))
                .build();
    }

    public static ModifyRecruitmentPositionRequest createForExceedSizeFail() {
        return ModifyRecruitmentPositionRequest.builder()
                .recruitmentPositionInfosToAdd(List.of(
                        CreateRecruitmentPositionInfoFixture.create(),
                        CreateRecruitmentPositionInfoFixture.create(),
                        CreateRecruitmentPositionInfoFixture.create(),
                        CreateRecruitmentPositionInfoFixture.create(),
                        CreateRecruitmentPositionInfoFixture.create(),
                        CreateRecruitmentPositionInfoFixture.create(),
                        CreateRecruitmentPositionInfoFixture.create()
                ))
                .build();
    }

    public static ModifyRecruitmentPositionRequest createForUnderSizeFail() {
        return ModifyRecruitmentPositionRequest.builder()
                .recruitmentPositionIdsToRemove(List.of(1L, 2L, 3L, 4L))
                .build();
    }

    public static class UpdateRecruitmentPositionInfoFixture {

        public static UpdateRecruitmentPositionInfo create(long id) {
            return UpdateRecruitmentPositionInfo.builder()
                    .recruitmentPositionId(id)
                    .title(ETC)
                    .headcount(1)
                    .build();
        }
    }
}
