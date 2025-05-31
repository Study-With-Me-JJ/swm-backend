package com.jj.swm.domain.study.core.dto.component;

import com.jj.swm.domain.study.core.entity.StudyRecruitmentPosition.RecruitmentPositionTitle;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import static com.jj.swm.domain.study.core.constants.StudyConstants.RECRUITMENT_POSITION_HEADCOUNT_MAX;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateRecruitmentPositionInfo {

    @NotNull
    private RecruitmentPositionTitle title;

    @NotNull
    @Positive
    @Max(RECRUITMENT_POSITION_HEADCOUNT_MAX)
    private Integer headcount;
}
