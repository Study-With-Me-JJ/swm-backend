package com.jj.swm.domain.study.repository;

import com.jj.swm.domain.study.entity.StudyRecruitmentPosition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyRecruitmentPositionRepository extends
        JpaRepository<StudyRecruitmentPosition, Long>, JdbcStudyRecruitmentPositionRepository {
}
