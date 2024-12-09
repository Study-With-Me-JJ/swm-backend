package com.jj.swm.domain.study.repository;

import com.jj.swm.domain.study.entity.Study;
import com.jj.swm.domain.study.entity.StudyRecruitmentPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StudyRecruitmentPositionRepository extends
        JpaRepository<StudyRecruitmentPosition, Long>, JdbcStudyRecruitmentPositionRepository {

    List<StudyRecruitmentPosition> findAllByIdInAndStudy(List<Long> recruitPositionIds, Study study);

    @Modifying
    @Query("update StudyRecruitmentPosition srp set srp.deletedAt = CURRENT_TIMESTAMP " +
            "where srp.id in ?1 and srp.study.id = ?2")
    void deleteAllByIdInAndStudyId(List<Long> deleteRecruitPositionIds, Long studyId);
}
