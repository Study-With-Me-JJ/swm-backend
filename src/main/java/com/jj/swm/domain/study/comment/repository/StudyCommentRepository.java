package com.jj.swm.domain.study.comment.repository;

import com.jj.swm.domain.study.comment.repository.dto.StudyParentCommentChildrenCountInfo;
import com.jj.swm.domain.study.comment.entity.StudyComment;
import com.jj.swm.domain.study.comment.repository.custom.CustomStudyCommentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StudyCommentRepository extends JpaRepository<StudyComment, Long>, CustomStudyCommentRepository {

    @Query("select c from StudyComment c left join fetch c.parent where c.id = ?1")
    Optional<StudyComment> findByIdWithParent(Long id);

    Optional<StudyComment> findByIdAndUserId(Long id, UUID userId);

    @Query("select c from StudyComment c join fetch c.user where c.study.id = ?1 and c.parent.id is null")
    Page<StudyComment> findPagedParentByStudyIdWithUser(Long studyId, Pageable pageable);

    @Query("""
            select c.parent.id as parentId, count(c) as childrenCount
            from StudyComment c
            where c.parent.id in ?1
            group by c.parent.id
            """
    )
    List<StudyParentCommentChildrenCountInfo> countByParentIds(List<Long> parentIds);

    @Modifying
    @Query("update StudyComment c set c.deletedAt = CURRENT_TIMESTAMP where c.id = ?1 or c.parent.id = ?1")
    void deleteWithChildrenById(Long id);

    @Modifying
    @Query("update StudyComment c set c.deletedAt = CURRENT_TIMESTAMP where c.study.id = ?1")
    void deleteAllByStudyId(Long studyId);

    @Modifying
    @Query("update StudyComment c set c.deletedAt = CURRENT_TIMESTAMP where c.study.id in ?1")
    void deleteAllByStudyIds(List<Long> studyIds);
}
