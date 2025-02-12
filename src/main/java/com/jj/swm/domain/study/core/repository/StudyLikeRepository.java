package com.jj.swm.domain.study.core.repository;

import com.jj.swm.domain.study.core.entity.Study;
import com.jj.swm.domain.study.core.entity.StudyLike;
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

    @Query("select l.study from StudyLike l where l.user.id = ?1")
    Page<Study> findPagedStudyByUserId(UUID userId, Pageable pageable);
}
