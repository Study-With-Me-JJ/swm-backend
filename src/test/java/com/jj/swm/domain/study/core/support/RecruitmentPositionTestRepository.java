package com.jj.swm.domain.study.core.support;

import com.jj.swm.domain.study.core.entity.StudyRecruitmentPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

@ActiveProfiles("test")
public interface RecruitmentPositionTestRepository extends JpaRepository<StudyRecruitmentPosition, Long> {

    long countByStudyId(Long studyId);

    Optional<StudyRecruitmentPosition> findFirstByStudyId(Long studyId);
}
