package com.jj.swm.domain.study.comment.repository;

import com.jj.swm.domain.study.comment.entity.StudyComment;

import java.util.List;

public interface CustomCommentRepository {

    List<StudyComment> findPagedReplyListByParentIdWithUser(
            int pageSize,
            Long parentId,
            Long lastReplyId
    );
}
