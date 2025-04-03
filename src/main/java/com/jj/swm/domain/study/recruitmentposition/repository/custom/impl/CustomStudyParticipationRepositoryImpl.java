package com.jj.swm.domain.study.recruitmentposition.repository.custom.impl;

import com.jj.swm.domain.common.utils.QueryDSLBooleanUtils;
import com.jj.swm.domain.study.recruitmentposition.dto.GetStudyParticipationCondition;
import com.jj.swm.domain.study.recruitmentposition.entity.StudyParticipation;
import com.jj.swm.domain.study.recruitmentposition.repository.custom.CustomStudyParticipationRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.jj.swm.domain.study.recruitmentposition.entity.QStudyParticipation.studyParticipation;

@RequiredArgsConstructor
public class CustomStudyParticipationRepositoryImpl implements CustomStudyParticipationRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<StudyParticipation> findPagedStudyParticipationByConditionWithUser(
            Long recruitmentPositionId,
            GetStudyParticipationCondition condition,
            int pageSize
    ) {
        return jpaQueryFactory.selectFrom(studyParticipation)
                .join(studyParticipation.user)
                .fetchJoin()
                .where(
                        studyParticipation.recruitmentPosition.id.eq(recruitmentPositionId),
                        lastStudyParticipationIdGt(condition.getLastStudyParticipationId()),
                        studyParticipation.status.eq(condition.getStatus())
                )
                .orderBy(studyParticipation.id.asc())
                .limit(pageSize)
                .fetch();
    }

    private BooleanBuilder lastStudyParticipationIdGt(Long lastStudyParticipationId) {
        return QueryDSLBooleanUtils.nullSafeBuilder(() -> studyParticipation.id.gt(lastStudyParticipationId));
    }
}
