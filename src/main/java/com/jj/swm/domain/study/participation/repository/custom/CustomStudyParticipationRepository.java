package com.jj.swm.domain.study.participation.repository.custom;

import com.jj.swm.domain.study.participation.entity.StudyParticipation;
import com.jj.swm.domain.study.participation.entity.StudyParticipationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomStudyParticipationRepository {

    Page<StudyParticipation> findPagedStudyParticipationByStatusWithUser(
            Long recruitmentPositionId,
            StudyParticipationStatus status,
            Pageable pageable
    );
}
