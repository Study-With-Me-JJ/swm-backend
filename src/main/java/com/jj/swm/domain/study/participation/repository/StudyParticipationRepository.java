package com.jj.swm.domain.study.participation.repository;

import com.jj.swm.domain.study.core.entity.Study;
import com.jj.swm.domain.study.participation.entity.StudyParticipation;
import com.jj.swm.domain.study.participation.repository.custom.CustomStudyParticipationRepository;
import com.jj.swm.domain.study.participation.repository.dto.PositionAcceptedParticipationCountInfo;
import com.jj.swm.domain.study.participation.repository.dto.StudyParticipationCountInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface StudyParticipationRepository extends
        JpaRepository<StudyParticipation, Long>, CustomStudyParticipationRepository {

    @Query("select p.id from StudyParticipation p where p.recruitmentPosition.id in (?1)")
    List<Long> findIdsByRecruitmentPositionIds(List<Long> recruitmentPositionIds);

    @Query("select p.id from StudyParticipation p where p.study.id in (?1)")
    List<Long> findIdsByStudyIds(List<Long> studyIds);

    @Query(
            value = "select * from study_participation where study_id = ?1 and user_id = ?2 order by id desc limit 1",
            nativeQuery = true
    )
    Optional<StudyParticipation> findByStudyIdAndUserIdWithNativeQuery(Long studyId, UUID userId);

    @Query("""
            select p
            from StudyParticipation p
            join fetch p.recruitmentPosition
            where p.study.id in (?1) and p.user.id = ?2
            """)
    List<StudyParticipation> findAllByStudyIdsAndUserIdWithPosition(Set<Long> studyId, UUID userId);

    @Query("select p from StudyParticipation p where p.study.id = ?1 and p.user.id = ?2")
    Optional<StudyParticipation> findByStudyIdAndUserId(Long studyId, UUID userId);

    @Query("select p.study from StudyParticipation p where p.user.id = ?1")
    Page<Study> findPagedStudyByUserId(UUID userId, Pageable pageable);

    @Query("select p from StudyParticipation p join fetch p.study join fetch p.recruitmentPosition where p.id = ?1")
    Optional<StudyParticipation> findByIdWithStudyAndPosition(Long id);

    @Query("select p from StudyParticipation p join fetch p.user join fetch p.study where p.id = ?1")
    Optional<StudyParticipation> findByIdWithUserAndStudy(Long id);

    Optional<StudyParticipation> findByIdAndUserId(Long id, UUID userId);

    @Query("select count(*) from StudyParticipation p where p.recruitmentPosition.id = ?1 and p.status = 'ACCEPTED'")
    long countByRecruitmentPositionIdAndAccepted(Long recruitmentPositionId);

    @Query("""
            select p.recruitmentPosition.id as recruitmentPositionId, count(*) as acceptedCount
            from StudyParticipation p
            where p.recruitmentPosition.id in ?1 and p.status = 'ACCEPTED'
            group by p.recruitmentPosition.id
            """)
    List<PositionAcceptedParticipationCountInfo> countByRecruitmentPositionIdsAndAccepted(
            List<Long> recruitmentPositionIds
    );

    @Query("""
            select p.recruitmentPosition.id as recruitmentPositionId, count(*) as participatedCount,
                       sum(case when p.status = 'ACCEPTED' then 1 else 0 end) as acceptedCount
            from StudyParticipation p
            where p.recruitmentPosition.id in (?1)
            group by p.recruitmentPosition.id
            """)
    List<StudyParticipationCountInfo> countByRecruitmentPositionIds(List<Long> recruitmentPositionIds);

    @Modifying
    @Query("update StudyParticipation p set p.deletedAt = CURRENT_TIMESTAMP where p.recruitmentPosition.id in (?1)")
    void deleteAllByRecruitmentPositionIds(List<Long> recruitmentPositionIds);

    @Modifying
    @Query("update StudyParticipation p set p.deletedAt = CURRENT_TIMESTAMP where p.study.id in (?1)")
    void deleteAllByStudyIds(List<Long> studyIds);
}
