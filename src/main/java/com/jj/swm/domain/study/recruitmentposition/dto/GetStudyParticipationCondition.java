package com.jj.swm.domain.study.recruitmentposition.dto;

import com.jj.swm.domain.study.recruitmentposition.entity.StudyParticipationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetStudyParticipationCondition {

    @Schema(defaultValue = "PENDING")
    private StudyParticipationStatus status = StudyParticipationStatus.PENDING;

    private Long lastStudyParticipationId;
}
