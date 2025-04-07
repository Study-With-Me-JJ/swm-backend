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

    List<StudyRecruitmentPosition> findByIdInAndStudyUserId(List<Long> recruitmentPositionIds, UUID userId);

    @Modifying
    @Query("update StudyRecruitmentPosition rp set rp.deletedAt = CURRENT_TIMESTAMP where rp.study.id = ?1")
    void deleteAllByStudyId(Long studyId);

    @Modifying
    @Query("update StudyRecruitmentPosition rp set rp.deletedAt = CURRENT_TIMESTAMP where rp.study.id in ?1")
    void deleteAllByStudyIds(List<Long> studyIds);

    int countByStudyId(Long studyId);

    int countByIdInAndStudyUserId(List<Long> ids, UUID userId);

    @Query("select rp from StudyRecruitmentPosition rp join fetch rp.study where rp.id = ?1")
    Optional<StudyRecruitmentPosition> findByIdWithStudy(Long recruitmentPositionId);

    @Modifying
    @Query("update StudyRecruitmentPosition rp set rp.deletedAt = CURRENT_TIMESTAMP where rp.id in ?1")
    void deleteAllByIds(List<Long> ids);

    List<StudyRecruitmentPosition> findByIdNotInAndStudyId(List<Long> ids, Long studyId);
}
