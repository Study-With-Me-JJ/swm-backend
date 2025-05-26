package com.jj.swm.domain.study.core.repository;

import com.jj.swm.domain.study.core.entity.Study;
import com.jj.swm.domain.study.core.entity.StudyBookmark;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StudyBookmarkRepository extends JpaRepository<StudyBookmark, Long> {

    Optional<StudyBookmark> findByIdAndUserId(Long id, UUID userId);

    List<StudyBookmark> findAllByStudyIdInAndUserId(List<Long> studyIds, UUID userId);

    @Query("select b.study from StudyBookmark b where b.user.id = ?1")
    Page<Study> findPagedStudyByUserId(UUID userId, Pageable pageable);

    @Query(
            value = "select b.id from study_bookmark b where b.study_id = ?1 and b.user_id = ?2 limit 1",
            nativeQuery = true
    )
    Long findIdByStudyIdAndUserId(Long studyId, UUID userId);

    boolean existsByStudyIdAndUserId(Long studyId, UUID userId);

    @Modifying
    @Query("delete from StudyBookmark b where b.study.id in ?1")
    void deleteAllByStudyIds(List<Long> studyIds);
}
