package com.jj.swm.domain.study.repository;

import com.jj.swm.domain.study.entity.StudyComment;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.function.Supplier;

import static com.jj.swm.domain.study.entity.QStudyComment.studyComment;

@RequiredArgsConstructor
public class CustomCommentRepositoryImpl implements CustomCommentRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public List<StudyComment> findAllWithUserByParentId(
            int pageSize,
            Long parentId,
            Long lastReplyId
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
        return this.nullSafeBuilder(() -> studyComment.id.lt(lastReplyId));
    }

    private BooleanBuilder nullSafeBuilder(Supplier<BooleanExpression> f) {
        try {
            return new BooleanBuilder(f.get());
        } catch (IllegalArgumentException | NullPointerException e) {
            return null;
        }
    }
}
