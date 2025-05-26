package com.jj.swm.domain.study.participation.repository.custom;

import com.jj.swm.domain.study.participation.entity.StudyParticipation;
import com.jj.swm.domain.study.participation.entity.StudyParticipation.StudyParticipationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomStudyParticipationRepository {

    Page<StudyParticipation> findPagedParticipationByStatusWithUser(
            Long recruitmentPositionId,
            StudyParticipationStatus status,
            Pageable pageable
    );

    Page<StudyParticipation> findPagedParticipationByStatusWithUserInMyPage(
            Long studyId,
            StudyParticipationStatus status,
            Pageable pageable
    );
}
