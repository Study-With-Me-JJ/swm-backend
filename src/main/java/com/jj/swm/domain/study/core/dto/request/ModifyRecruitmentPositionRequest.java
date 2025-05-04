package com.jj.swm.domain.study.core.dto.request;

import com.jj.swm.domain.study.core.dto.component.CreateRecruitmentPositionInfo;
import com.jj.swm.domain.study.core.entity.StudyRecruitmentPosition.RecruitmentPositionTitle;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

import static com.jj.swm.domain.study.core.constants.StudyConstants.RECRUITMENT_POSITION_HEADCOUNT_MAX;
import static com.jj.swm.domain.study.core.constants.StudyConstants.RECRUITMENT_POSITION_LIMIT;

@Getter
@SuperBuilder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ModifyRecruitmentPositionRequest {

    @Valid
    @Size(max = RECRUITMENT_POSITION_LIMIT)
    private List<CreateRecruitmentPositionInfo> createRecruitmentPositionInfos;

    @Valid
    @Size(max = RECRUITMENT_POSITION_LIMIT)
    private List<UpdateRecruitmentPositionInfo> updateRecruitmentPositionInfos;

    @Size(max = RECRUITMENT_POSITION_LIMIT)
    private List<Long> recruitmentPositionIdsToRemove;

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class UpdateRecruitmentPositionInfo {

        @NotNull
        private Long recruitmentPositionId;

        @NotNull
        private RecruitmentPositionTitle title;

        @NotNull
        @Positive
        @Max(RECRUITMENT_POSITION_HEADCOUNT_MAX)
        private Integer headcount;
    }
}
