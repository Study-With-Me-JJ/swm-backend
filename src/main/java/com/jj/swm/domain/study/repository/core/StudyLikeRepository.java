package com.jj.swm.domain.study.repository.core;

import com.jj.swm.domain.study.entity.core.Study;
import com.jj.swm.domain.study.entity.core.StudyLike;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Query("select sl.study from StudyLike sl where sl.user.id = ?1")
    Page<Study> findStudiesByUserId(UUID userId, Pageable pageable);
}
