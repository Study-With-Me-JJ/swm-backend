package com.jj.swm.domain.study.repository;

import com.jj.swm.domain.study.entity.StudyLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;


public interface StudyLikeRepository extends JpaRepository<StudyLike, Long> {

    Optional<StudyLike> findByUserIdAndStudyId(UUID userId, Long studyId);

    boolean existsByUserIdAndStudyId(UUID userId, Long studyId);

    @Modifying
    @Query("delete from StudyLike l where l.study.id = ?1")
    void deleteAllByStudyId(Long studyId);
}
