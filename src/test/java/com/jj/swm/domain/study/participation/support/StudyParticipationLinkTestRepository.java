package com.jj.swm.domain.study.participation.support;

import com.jj.swm.domain.study.participation.entity.StudyParticipationLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
public interface StudyParticipationLinkTestRepository extends JpaRepository<StudyParticipationLink, Long> {

    long countByParticipationId(Long participationId);
}
