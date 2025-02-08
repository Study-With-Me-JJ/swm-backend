package com.jj.swm.domain.study.repository.comment;

import com.jj.swm.domain.study.entity.comment.StudyComment;

import java.util.List;

public interface CustomCommentRepository {

    List<StudyComment> findPagedWithUserByParentId(
            int pageSize,
            Long parentId,
            Long lastReplyId
    );
}
