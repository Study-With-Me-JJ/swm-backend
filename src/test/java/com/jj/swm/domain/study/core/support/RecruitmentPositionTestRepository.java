package com.jj.swm.domain.study.core.support;

import com.jj.swm.domain.study.core.entity.StudyRecruitmentPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

@ActiveProfiles("test")
public interface RecruitmentPositionTestRepository extends JpaRepository<StudyRecruitmentPosition, Long> {

    Optional<StudyRecruitmentPosition> findFirstByStudyId(Long studyId);

    Optional<StudyRecruitmentPosition> findFirstByIdNotAndStudyId(Long id, Long studyId);

    long countByStudyId(Long studyId);
}
