package com.jj.swm.domain.study.participation.support;

import com.jj.swm.domain.study.participation.entity.StudyParticipation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@ActiveProfiles("test")
public interface StudyParticipationTestRepository extends JpaRepository<StudyParticipation, Long> {

    List<StudyParticipation> findAllByRecruitmentPositionId(Long recruitmentPositionId);

    List<StudyParticipation> findAllByOrderById();
}
