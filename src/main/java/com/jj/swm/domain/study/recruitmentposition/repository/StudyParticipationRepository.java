package com.jj.swm.domain.study.recruitmentposition.repository;

import com.jj.swm.domain.study.recruitmentposition.dto.AcceptedStudyParticipationCountInfo;
import com.jj.swm.domain.study.recruitmentposition.entity.StudyParticipation;
import com.jj.swm.domain.study.recruitmentposition.repository.custom.CustomStudyParticipationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StudyParticipationRepository extends
        JpaRepository<StudyParticipation, Long>, CustomStudyParticipationRepository {

    @Query("select count(*) from StudyParticipation p where p.recruitmentPosition.id = ?1 and p.status = 'ACCEPTED'")
    int countByRecruitmentPositionIdAndAcceptedStatus(Long recruitmentPositionId);

    @Query("""
            select p.recruitmentPosition.id as recruitmentPositionId, count(*) as acceptedStudyParticipationCount
            from StudyParticipation p
            where p.recruitmentPosition.id in ?1 and p.status = 'ACCEPTED'
            group by p.recruitmentPosition.id
            """
    )
    List<AcceptedStudyParticipationCountInfo> countByRecruitmentPositionIdsAndAcceptedStatus(
            List<Long> recruitmentPositionIds
    );

    @Query("select p from StudyParticipation p join fetch p.study where p.id = ?1")
    Optional<StudyParticipation> findByIdWithStudy(Long id);

    @Query("select p from StudyParticipation p join fetch p.user join fetch p.study where p.id = ?1")
    Optional<StudyParticipation> findByIdWithUserAndStudy(Long id);

    Optional<StudyParticipation> findByIdAndUserId(Long id, UUID userId);

    @Modifying
    @Query("update StudyParticipation p set p.deletedAt = CURRENT_TIMESTAMP where p.recruitmentPosition.id = ?1")
    void deleteAllByRecruitmentPositionId(Long recruitmentPositionId);

    @Modifying
    @Query("update StudyParticipation p set p.deletedAt = CURRENT_TIMESTAMP where p.study.id = ?1")
    void deleteAllByStudyId(Long studyId);

    @Modifying
    @Query("update StudyParticipation p set p.deletedAt = CURRENT_TIMESTAMP where p.study.id in (?1)")
    void deleteAllByStudyIds(List<Long> studyIds);

    @Query("select p.id from StudyParticipation p where p.recruitmentPosition.id = ?1")
    List<Long> findIdsByRecruitmentPositionId(Long recruitmentPositionId);

    @Query("select p.id from StudyParticipation p where p.study.id = ?1")
    List<Long> findIdsByStudyId(Long studyId);

    @Query("select p.id from StudyParticipation p where p.study.id in (?1)")
    List<Long> findIdsByStudyIds(List<Long> studyIds);

    @Query(
            value = "select * from study_participation where study_id = ?1 and user_id = ?2 order by id desc limit 1",
            nativeQuery = true
    )
    Optional<StudyParticipation> findByStudyIdAndUserId(Long studyId, UUID userId);
}
