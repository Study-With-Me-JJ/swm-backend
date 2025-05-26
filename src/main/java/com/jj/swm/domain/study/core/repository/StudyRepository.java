package com.jj.swm.domain.study.core.repository;

import com.jj.swm.domain.study.core.entity.Study;
import com.jj.swm.domain.study.core.repository.custom.CustomStudyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StudyRepository extends JpaRepository<Study, Long>, CustomStudyRepository {

    Optional<Study> findByIdAndUserId(Long id, UUID userId);

    @Query("select s.id from Study s where s.user.id = ?1")
    List<Long> findIdsByUserId(UUID userId);

    Page<Study> findAllByUserId(UUID userId, Pageable pageable);

    @Query("select s from Study s join fetch s.studyRecruitmentPositions where s.id = ?1")
    Optional<Study> findByIdWithRecruitmentPosition(Long id);

    long countByIdInAndUserId(List<Long> ids, UUID userId);

    @Modifying
    @Query("update Study s set s.deletedAt = CURRENT_TIMESTAMP where s.id in ?1")
    void deleteAllByIds(List<Long> ids);

    @Modifying
    @Query("update Study s set s.statistics.likeCount = s.statistics.likeCount + 1 where s.id = ?1")
    void incrementLikeCountById(Long id);

    @Modifying
    @Query("update Study s set s.statistics.likeCount = GREATEST(s.statistics.likeCount - 1, 0) where s.id = ?1")
    void decrementLikeCountById(Long id);

    @Modifying
    @Query("update Study s set s.statistics.commentCount = s.statistics.commentCount + 1 where s.id = ?1")
    void incrementCommentCountById(Long id);

    @Modifying
    @Query("update Study s set s.statistics.commentCount = GREATEST(s.statistics.commentCount - 1, 0) where s.id = ?1")
    void decrementCommentCountById(Long id);

    @Modifying
    @Query("update Study s set s.statistics.viewCount = s.statistics.viewCount + 1 where s.id = ?1")
    void incrementViewCountById(Long id);
}
