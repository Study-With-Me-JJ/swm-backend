package com.jj.swm.domain.study.core.support;

import com.jj.swm.domain.study.core.entity.StudyImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
public interface StudyImageTestRepository extends JpaRepository<StudyImage, Long> {

    long countByStudyId(Long studyId);
}
