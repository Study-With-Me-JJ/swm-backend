package com.jj.swm.domain.study.comment.repository;

import com.jj.swm.domain.study.comment.dto.ReplyCountInfo;
import com.jj.swm.domain.study.comment.entity.StudyComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<StudyComment, Long>, CustomCommentRepository {

    Optional<StudyComment> findByIdAndUserId(Long commentId, UUID userId);

    @Query("select c from StudyComment c left join fetch c.parent where c.id = ?1")
    Optional<StudyComment> findByIdWithParent(Long id);

    @Modifying
    @Query("update StudyComment c set c.deletedAt = CURRENT_TIMESTAMP where c.id = ?1 or c.parent.id = ?1")
    void deleteAllByIdOrParentId(Long commentId);

    @Query("select c from StudyComment c left join fetch c.parent where c.id = ?1 and c.user.id = ?2")
    Optional<StudyComment> findByIdAndUserIdWithParent(Long commentId, UUID userId);

    @Query("select c from StudyComment c join fetch c.user where c.study.id = ?1 and c.parent.id is null")
    Page<StudyComment> findAllByStudyIdWithUser(Long studyId, Pageable pageable);

    @Query("""
            SELECT c.parent.id as parentId, count(c) as replyCount
            FROM StudyComment c
            WHERE c.parent.id in ?1
            GROUP BY c.parent.id
            """
    )
    List<ReplyCountInfo> countRepliesByParentIds(List<Long> parentIds);

    @Modifying
    @Query("update StudyComment c set c.deletedAt = CURRENT_TIMESTAMP where c.study.id = ?1")
    void deleteAllByStudyId(Long studyId);
}
