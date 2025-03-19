package com.jj.swm.domain.study.comment.repository.custom;

import com.jj.swm.domain.study.comment.entity.StudyComment;

import java.util.List;

public interface CustomStudyCommentRepository {

    List<StudyComment> findPagedReplyByParentIdWithUser(
            Long parentId,
            Long lastReplyId,
            int pageSize
    );
}
