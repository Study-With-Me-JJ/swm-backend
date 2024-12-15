package com.jj.swm.domain.study.repository;

import com.jj.swm.domain.study.entity.StudyParticipant;
import com.jj.swm.domain.study.entity.StudyRecruitmentPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface StudyParticipantRepository extends JpaRepository<StudyParticipant, Long> {

    Optional<StudyParticipant> findByUserIdAndStudyRecruitmentPositionId(UUID userId, Long recruitmentPositionId);

    Optional<StudyParticipant> findByIdAndUserId(Long id, UUID userId);

    @Query("select count(p) from StudyParticipant p where p.status = 'ACCEPTED' and p.studyRecruitmentPosition = ?1")
    Integer countAcceptedByRecruitmentPositionId(StudyRecruitmentPosition recruitmentPosition);
}
