package com.jj.swm.domain.study.repository;

import com.jj.swm.domain.study.entity.StudyRecruitmentPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StudyRecruitmentPositionRepository extends
        JpaRepository<StudyRecruitmentPosition, Long>, JdbcStudyRecruitmentPositionRepository {

    @Query("select srp from StudyRecruitmentPosition srp where srp.study.id = ?1")
    List<StudyRecruitmentPosition> findAllByStudyId(Long studyId);

    @Query("select srp from StudyRecruitmentPosition srp where srp.id = ?1 and srp.study.user.id = ?2")
    Optional<StudyRecruitmentPosition> findByIdAndUserId(Long recruitPositionId, UUID userId);

    @Modifying
    @Query("delete from StudyRecruitmentPosition srp where srp.id = ?1 and srp.study.user.id = ?2")
    void deleteByIdAndUserId(Long recruitPositionId, UUID userId);
}
