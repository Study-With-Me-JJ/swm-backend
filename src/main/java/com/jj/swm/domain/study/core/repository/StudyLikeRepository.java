package com.jj.swm.domain.study.core.repository;

import com.jj.swm.domain.study.core.entity.Study;
import com.jj.swm.domain.study.core.entity.StudyLike;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;


public interface StudyLikeRepository extends JpaRepository<StudyLike, Long> {

    Optional<StudyLike> findByIdAndUserId(Long id, UUID userId);

    List<StudyLike> findAllByStudyIdInAndUserId(Set<Long> studyIds, UUID userId);

    @Query(value = "select l.id from study_like l where l.study_id = ?1 and l.user_id = ?2 limit 1", nativeQuery = true)
    Long findIdByStudyIdAndUserId(Long studyId, UUID userId);

    @Query("select l.study from StudyLike l where l.user.id = ?1")
    Page<Study> findPagedStudyByUserId(UUID userId, Pageable pageable);

    boolean existsByStudyIdAndUserId(Long studyId, UUID userId);

    @Modifying
    @Query("delete from StudyLike l where l.study.id in ?1")
    void deleteAllByStudyIds(List<Long> studyId);
}
