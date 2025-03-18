package com.jj.swm.domain.study.recruitmentposition.repository;

import com.jj.swm.domain.study.recruitmentposition.entity.StudyParticipationLink;
import com.jj.swm.domain.study.recruitmentposition.repository.jdbc.JdbcStudyParticipationLinkRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyParticipationLinkRepository extends
        JpaRepository<StudyParticipationLink, Long>, JdbcStudyParticipationLinkRepository {
}
