package com.jj.swm.domain.study.repository;

import com.jj.swm.domain.study.entity.StudyParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface StudyParticipantRepository extends JpaRepository<StudyParticipant, Long> {

    Optional<StudyParticipant> findByUserIdAndStudyRecruitmentPositionId(UUID userId, Long recruitmentPositionId);
}
