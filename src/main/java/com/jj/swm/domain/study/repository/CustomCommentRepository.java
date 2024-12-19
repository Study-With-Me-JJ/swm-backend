package com.jj.swm.domain.study.repository;

import com.jj.swm.domain.study.entity.StudyComment;

import java.util.List;

public interface CustomCommentRepository {

    List<StudyComment> findPagedWithUserByParentId(
            int pageSize,
            Long parentId,
            Long lastReplyId
    );
}
