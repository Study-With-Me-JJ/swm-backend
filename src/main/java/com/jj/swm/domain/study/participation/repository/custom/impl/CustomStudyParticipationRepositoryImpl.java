package com.jj.swm.domain.study.participation.repository.custom.impl;

import com.jj.swm.domain.common.utils.QueryDSLBooleanUtils;
import com.jj.swm.domain.study.participation.entity.StudyParticipation;
import com.jj.swm.domain.study.participation.entity.StudyParticipationStatus;
import com.jj.swm.domain.study.participation.repository.custom.CustomStudyParticipationRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static com.jj.swm.domain.study.participation.entity.QStudyParticipation.studyParticipation;

@RequiredArgsConstructor
public class CustomStudyParticipationRepositoryImpl implements CustomStudyParticipationRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<StudyParticipation> findPagedStudyParticipationByStatusWithUser(
            Long recruitmentPositionId,
            StudyParticipationStatus status,
            Pageable pageable
    ) {
        List<StudyParticipation> content = jpaQueryFactory.selectFrom(studyParticipation)
                .join(studyParticipation.user)
                .fetchJoin()
                .where(
                        studyParticipation.recruitmentPosition.id.eq(recruitmentPositionId),
                        statusEq(status)
                ).orderBy(studyParticipation.id.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory.select(studyParticipation.count())
                .from(studyParticipation)
                .where(
                        studyParticipation.recruitmentPosition.id.eq(recruitmentPositionId),
                        statusEq(status)
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public Page<StudyParticipation> findPagedStudyParticipationByStatusWithUserInMyPage(
            Long studyId,
            StudyParticipationStatus status,
            Pageable pageable
    ) {
        List<StudyParticipation> content = jpaQueryFactory.selectFrom(studyParticipation)
                .join(studyParticipation.user)
                .fetchJoin()
                .join(studyParticipation.recruitmentPosition)
                .fetchJoin()
                .where(
                        studyParticipation.study.id.eq(studyId),
                        statusEq(status)
                ).orderBy(studyParticipation.id.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory.select(studyParticipation.count())
                .from(studyParticipation)
                .where(
                        studyParticipation.study.id.eq(studyId),
                        statusEq(status)
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private BooleanBuilder statusEq(StudyParticipationStatus status) {
        return QueryDSLBooleanUtils.nullSafeBuilder(() -> studyParticipation.status.eq(status));
    }
}
