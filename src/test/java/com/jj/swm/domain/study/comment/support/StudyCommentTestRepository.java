package com.jj.swm.domain.study.comment.support;

import com.jj.swm.domain.study.comment.entity.StudyComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@ActiveProfiles("test")
public interface StudyCommentTestRepository extends JpaRepository<StudyComment, Long> {

    List<StudyComment> findAllByOrderByIdDesc();

    List<StudyComment> findAllByParentIdOrderByIdDesc(Long parentId);
}
