package com.jj.swm.domain.study.comment.repository.custom.impl;

import com.jj.swm.domain.study.comment.entity.StudyComment;
import com.jj.swm.domain.study.comment.repository.custom.CustomCommentRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.jj.swm.domain.common.utils.QueryDSLBooleanUtils.nullSafeBuilder;
import static com.jj.swm.domain.study.comment.entity.QStudyComment.studyComment;

@RequiredArgsConstructor
public class CustomCommentRepositoryImpl implements CustomCommentRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public List<StudyComment> findPagedReplyByParentIdWithUser(
            Long parentId,
            Long lastReplyId,
            int pageSize
    ) {
        return jpaQueryFactory.selectFrom(studyComment)
                .join(studyComment.user)
                .fetchJoin()
                .where(studyComment.parent.id.eq(parentId), lastReplyIdLt(lastReplyId))
                .orderBy(studyComment.id.desc())
                .limit(pageSize)
                .fetch();
    }

    private BooleanBuilder lastReplyIdLt(Long lastReplyId) {
        return nullSafeBuilder(() -> studyComment.id.lt(lastReplyId));
    }
}
