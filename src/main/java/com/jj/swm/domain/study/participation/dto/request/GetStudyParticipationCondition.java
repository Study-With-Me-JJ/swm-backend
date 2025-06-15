package com.jj.swm.domain.study.participation.dto.request;

import com.jj.swm.domain.study.participation.entity.StudyParticipation.StudyParticipationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetStudyParticipationCondition {

    private StudyParticipationStatus status;

    @Schema(defaultValue = "0")
    private int pageNo = 0;
}
