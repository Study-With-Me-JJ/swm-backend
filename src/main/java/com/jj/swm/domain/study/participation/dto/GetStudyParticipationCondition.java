package com.jj.swm.domain.study.participation.dto;

import com.jj.swm.domain.study.participation.entity.StudyParticipationStatus;
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
