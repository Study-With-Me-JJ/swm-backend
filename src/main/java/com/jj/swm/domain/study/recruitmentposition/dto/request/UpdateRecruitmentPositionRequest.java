package com.jj.swm.domain.study.recruitmentposition.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import static com.jj.swm.domain.study.constants.StudyConstants.RECRUITMENT_POSITION_COUNT_MAX;

@Getter
@SuperBuilder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateRecruitmentPositionRequest extends CreateRecruitmentPositionRequest {

    @NotNull
    @PositiveOrZero
    @Max(RECRUITMENT_POSITION_COUNT_MAX)
    private Integer acceptedCount;
}
