package com.jj.swm.domain.study.repository;

import com.jj.swm.domain.study.dto.ParticipantStatusInfo;
import com.jj.swm.domain.study.dto.StudyPositionAcceptedCountInfo;
import com.jj.swm.domain.study.entity.StudyRecruitmentPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StudyRecruitmentPositionRepository extends
        JpaRepository<StudyRecruitmentPosition, Long>, JdbcStudyRecruitmentPositionRepository {

    List<StudyRecruitmentPosition> findAllByIdInAndStudyId(List<Long> recruitPositionIds, Long studyId);

    @Modifying
    @Query("update StudyRecruitmentPosition srp set srp.deletedAt = CURRENT_TIMESTAMP " +
            "where srp.id in ?1 and srp.study.id = ?2")
    void deleteAllByIdInAndStudyId(List<Long> deleteRecruitPositionIds, Long studyId);

    @Query(
            value = "select srp.id, srp.title, srp.headcount, count(sp.id) as acceptedCount " +
                    "from study_recruitment_position srp " +
                    "left join study_participant sp " +
                    "on srp.id = sp.study_recruitment_position_id " +
                    "where srp.study_id = ?1 and srp.deleted_at is null " +
                    "group by srp.id",
            nativeQuery = true
    )
    List<StudyPositionAcceptedCountInfo> findPositionAcceptedCountInfoByStudyId(Long studyId);

    @Query(
            value = "select srp.id, sp.status " +
                    "from study_recruitment_position srp left join study_participant sp " +
                    "on srp.id = sp.study_recruitment_position_id and sp.user_id = ?2 " +
                    "where srp.study_id = ?1 and srp.deleted_at is null",
            nativeQuery = true
    )
    List<ParticipantStatusInfo> findParticipantStatusByStudyIdAndUserId(Long studyId, UUID userId);

    @Query("select srp from StudyRecruitmentPosition srp where srp.id = ?1 and srp.study.user.id = ?2")
    Optional<StudyRecruitmentPosition> findByIdAndUserId(Long recruitPositionId, UUID userId);
}
