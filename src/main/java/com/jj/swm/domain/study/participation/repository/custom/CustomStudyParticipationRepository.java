package com.jj.swm.domain.study.participation.repository.custom;

import com.jj.swm.domain.study.participation.dto.GetStudyParticipationCondition;
import com.jj.swm.domain.study.participation.entity.StudyParticipation;

import java.util.List;

public interface CustomStudyParticipationRepository {

    List<StudyParticipation> findPagedStudyParticipationByConditionWithUser(
            Long recruitmentPositionId,
            GetStudyParticipationCondition condition,
            int pageSize
    );
}
