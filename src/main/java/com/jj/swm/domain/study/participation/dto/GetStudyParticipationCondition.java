package com.jj.swm.domain.study.participation.dto;

import com.jj.swm.domain.study.participation.entity.StudyParticipationStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetStudyParticipationCondition {

    private StudyParticipationStatus status;

    private int pageNo = 0;
}
