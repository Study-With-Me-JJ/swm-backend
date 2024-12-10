package com.jj.swm.domain.study.repository;

import com.jj.swm.domain.study.entity.StudyComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<StudyComment, Long> {

    Optional<StudyComment> findByIdAndUserId(Long commentId, UUID userId);
}
