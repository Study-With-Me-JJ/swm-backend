package com.jj.swm.domain.study.repository;

import com.jj.swm.domain.study.entity.StudyLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;


public interface StudyLikeRepository extends JpaRepository<StudyLike, Long> {

    Optional<StudyLike> findByUserIdAndStudyId(UUID userId, Long studyId);
}
