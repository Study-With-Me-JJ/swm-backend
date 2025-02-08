package com.jj.swm.domain.study.repository.comment;

import com.jj.swm.domain.study.dto.comment.ReplyCountInfo;
import com.jj.swm.domain.study.entity.comment.StudyComment;
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
    Optional<StudyComment> findWithParentById(Long id, UUID UserId);

    @Modifying
    @Query("update StudyComment c set c.deletedAt = CURRENT_TIMESTAMP where c.id = ?1 or c.parent.id = ?1")
    void deleteAllByIdOrParentId(Long commentId);

    @Query("select c from StudyComment c left join fetch c.parent where c.id = ?1 and c.user.id = ?2")
    Optional<StudyComment> findWithParentByIdAndUserId(Long commentId, UUID userId);

    @Query("select c from StudyComment c join fetch c.user where c.study.id = ?1 and c.parent.id is null")
    Page<StudyComment> findCommentWithUserByStudyId(Long studyId, Pageable pageable);

    @Query(
            value = "select c.parent_id, count(*) as replyCount " +
                    "from (select parent_id " +
                    "       from study_comment " +
                    "       where parent_id in ?1 " +
                    "       limit 100) as c " +
                    "group by c.parent_id",
            nativeQuery = true
    )
    List<ReplyCountInfo> countReplyByParentId(List<Long> parentIds);

    @Modifying
    @Query("update StudyComment c set c.deletedAt = CURRENT_TIMESTAMP where c.study.id = ?1")
    void deleteAllByStudyId(Long studyId);
}
