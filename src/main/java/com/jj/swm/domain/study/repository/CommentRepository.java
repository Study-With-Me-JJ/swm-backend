package com.jj.swm.domain.study.repository;

import com.jj.swm.domain.study.entity.StudyComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<StudyComment, Long> {

    Optional<StudyComment> findByIdAndUserId(Long commentId, UUID userId);

    @Query("select c from StudyComment c left join fetch c.parent where c.id = ?1 and c.deletedAt is null")
    Optional<StudyComment> findWithParentById(Long id, UUID UserId);

    @Modifying
    @Query("update StudyComment c set c.deletedAt = CURRENT_TIMESTAMP where c.id = ?1 or c.parent.id = ?1")
    void deleteAllByIdOrParentId(Long commentId);

    @Query("select c from StudyComment c left join fetch c.parent " +
            "where c.id = ?1 and c.user.id = ?2 and c.deletedAt is null")
    Optional<StudyComment> findWithParentByIdAndUserId(Long commentId, UUID userId);
}
