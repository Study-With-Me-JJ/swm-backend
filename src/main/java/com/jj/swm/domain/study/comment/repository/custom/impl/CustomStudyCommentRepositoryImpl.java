package com.jj.swm.domain.study.comment.repository.custom.impl;

import com.jj.swm.domain.common.utils.QueryDSLBooleanUtils;
import com.jj.swm.domain.study.comment.entity.StudyComment;
import com.jj.swm.domain.study.comment.repository.custom.CustomStudyCommentRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.jj.swm.domain.study.comment.entity.QStudyComment.studyComment;

@RequiredArgsConstructor
public class CustomStudyCommentRepositoryImpl implements CustomStudyCommentRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public List<StudyComment> findPagedChildByParentIdWithUser(
            Long parentId,
            Long lastChildId,
            int pageSize
    ) {
        return jpaQueryFactory.selectFrom(studyComment)
                .join(studyComment.user)
                .fetchJoin()
                .where(studyComment.parent.id.eq(parentId), lastChildIdLt(lastChildId))
                .orderBy(studyComment.id.desc())
                .limit(pageSize)
                .fetch();
    }

    private BooleanBuilder lastChildIdLt(Long lastChildId) {
        return QueryDSLBooleanUtils.nullSafeBuilder(() -> studyComment.id.lt(lastChildId));
    }
}
