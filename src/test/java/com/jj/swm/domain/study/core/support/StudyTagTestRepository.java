package com.jj.swm.domain.study.core.support;

import com.jj.swm.domain.study.core.entity.StudyTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
public interface StudyTagTestRepository extends JpaRepository<StudyTag, Long> {

    long countByStudyId(Long studyId);
}
