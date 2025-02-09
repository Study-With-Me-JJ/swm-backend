package com.jj.swm.domain.study.repository.comment;

import com.jj.swm.domain.study.entity.comment.StudyComment;

import java.util.List;

public interface CustomCommentRepository {

    List<StudyComment> findAllByParentIdWithUser(
            int pageSize,
            Long parentId,
            Long lastReplyId
    );
}
