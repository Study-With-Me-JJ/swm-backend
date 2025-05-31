package com.jj.swm.domain.study.core.repository;

import com.jj.swm.domain.study.core.entity.StudyRecruitmentPosition;
import com.jj.swm.domain.study.core.repository.jdbc.JdbcRecruitmentPositionRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RecruitmentPositionRepository extends
        JpaRepository<StudyRecruitmentPosition, Long>, JdbcRecruitmentPositionRepository {

    List<StudyRecruitmentPosition> findAllByStudyId(Long studyId);

    List<StudyRecruitmentPosition> findByStudyIdAndStudyUserId(Long studyId, UUID userId);

    @Query("select rp from StudyRecruitmentPosition rp join fetch rp.study where rp.id = ?1")
    Optional<StudyRecruitmentPosition> findByIdWithStudy(Long id);

    @Modifying
    @Query("update StudyRecruitmentPosition rp set rp.deletedAt = CURRENT_TIMESTAMP where rp.study.id in ?1")
    void deleteAllByStudyIds(List<Long> studyIds);

    @Modifying
    @Query("update StudyRecruitmentPosition rp set rp.deletedAt = CURRENT_TIMESTAMP where rp.id in ?1")
    void deleteAllByIds(List<Long> ids);
}
