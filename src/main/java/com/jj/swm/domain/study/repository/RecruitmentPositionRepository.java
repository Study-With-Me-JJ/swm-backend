package com.jj.swm.domain.study.repository;

import com.jj.swm.domain.study.entity.StudyRecruitmentPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RecruitmentPositionRepository extends
        JpaRepository<StudyRecruitmentPosition, Long>, JdbcRecruitmentPositionRepository {

    @Query("select rp from StudyRecruitmentPosition rp where rp.study.id = ?1")
    List<StudyRecruitmentPosition> findAllByStudyId(Long studyId);

    @Query("select rp from StudyRecruitmentPosition rp where rp.id = ?1 and rp.study.user.id = ?2")
    Optional<StudyRecruitmentPosition> findByIdAndStudyUserId(Long recruitPositionId, UUID userId);

    @Modifying
    @Query("update StudyRecruitmentPosition rp set rp.deletedAt = CURRENT_TIMESTAMP " +
            "where rp.id = ?1 and rp.study.user.id = ?2")
    void deleteByIdAndStudyUserId(Long recruitPositionId, UUID userId);

    @Modifying
    @Query("update StudyRecruitmentPosition rp set rp.deletedAt = CURRENT_TIMESTAMP where rp.study.id = ?1")
    void deleteAllByStudyId(Long studyId);
}
