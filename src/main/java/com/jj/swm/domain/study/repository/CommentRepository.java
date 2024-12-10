package com.jj.swm.domain.study.repository;

import com.jj.swm.domain.study.entity.StudyComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<StudyComment, Long> {
}
