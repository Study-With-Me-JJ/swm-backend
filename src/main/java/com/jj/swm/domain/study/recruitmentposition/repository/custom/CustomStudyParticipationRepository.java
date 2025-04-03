package com.jj.swm.domain.study.recruitmentposition.repository.custom;

import com.jj.swm.domain.study.recruitmentposition.dto.GetStudyParticipationCondition;
import com.jj.swm.domain.study.recruitmentposition.entity.StudyParticipation;

import java.util.List;

public interface CustomStudyParticipationRepository {

    List<StudyParticipation> findPagedStudyParticipationByConditionWithUser(
            Long recruitmentPositionId,
            GetStudyParticipationCondition condition,
            int pageSize
    );
}
