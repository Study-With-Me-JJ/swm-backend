package com.jj.swm.domain.study.core.repository.custom.impl;

import com.jj.swm.domain.common.utils.QueryDSLBooleanUtils;
import com.jj.swm.domain.study.core.dto.request.GetStudyCondition;
import com.jj.swm.domain.study.core.dto.request.GetStudyCondition.SortCriteria;
import com.jj.swm.domain.study.core.entity.Study;
import com.jj.swm.domain.study.core.entity.Study.StudyCategory;
import com.jj.swm.domain.study.core.entity.Study.StudyStatus;
import com.jj.swm.domain.study.core.entity.StudyRecruitmentPosition.RecruitmentPositionTitle;
import com.jj.swm.domain.study.core.repository.custom.CustomStudyRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.jj.swm.domain.study.core.entity.QStudy.study;
import static com.jj.swm.global.common.util.ListCheckUtils.isListPresent;


@RequiredArgsConstructor
public class CustomStudyRepositoryImpl implements CustomStudyRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Study> findPagedStudyByCondition(GetStudyCondition condition, int pageSize) {
        return jpaQueryFactory.selectFrom(study)
                .where(
                        studyTitleContains(condition.getTitle()),
                        studyCategoryEq(condition.getCategory()),
                        studyStatusEq(condition.getStatus()),
                        recruitmentPositionTitleExists(condition.getRecruitmentPositionTitles()),
                        buildSortPredicate(condition)
                )
                .orderBy(buildOrderSpecifier(condition.getSortCriteria()))
                .limit(pageSize)
                .fetch();
    }

    private BooleanBuilder studyTitleContains(String title) {
        return QueryDSLBooleanUtils.nullSafeBuilder(() -> study.title.contains(title));
    }

    private BooleanBuilder studyCategoryEq(StudyCategory category) {
        return QueryDSLBooleanUtils.nullSafeBuilder(() -> study.category.eq(category));
    }

    private BooleanBuilder studyStatusEq(StudyStatus status) {
        return QueryDSLBooleanUtils.nullSafeBuilder(() -> study.status.eq(status));
    }

    private BooleanBuilder recruitmentPositionTitleExists(List<RecruitmentPositionTitle> titles) {
        return isListPresent(titles)
                ? QueryDSLBooleanUtils.nullSafeBuilder(() -> study.studyRecruitmentPositions.any().title.in(titles))
                : null;
    }


    private BooleanBuilder buildSortPredicate(GetStudyCondition condition) {
        Integer lastSortValue = condition.getLastSortValue();
        Long lastId = condition.getLastStudyId();

        return switch (condition.getSortCriteria()) {
            case LIKE -> QueryDSLBooleanUtils.nullSafeBuilder(() -> study.statistics.likeCount.lt(lastSortValue)
                    .or(study.statistics.likeCount.eq(lastSortValue).and(study.id.lt(lastId))));
            case COMMENT -> QueryDSLBooleanUtils.nullSafeBuilder(() -> study.statistics.commentCount.lt(lastSortValue)
                    .or(study.statistics.commentCount.eq(lastSortValue).and(study.id.lt(lastId))));
            default -> QueryDSLBooleanUtils.nullSafeBuilder(() -> study.id.lt(lastId));
        };
    }

    private OrderSpecifier<?>[] buildOrderSpecifier(SortCriteria sortCriteria) {
        return switch (sortCriteria) {
            case LIKE -> new OrderSpecifier<?>[]{
                    new OrderSpecifier<>(Order.DESC, study.statistics.likeCount),
                    new OrderSpecifier<>(Order.DESC, study.id),
            };
            case NEWEST -> new OrderSpecifier<?>[]{
                    new OrderSpecifier<>(Order.DESC, study.id)
            };
            case COMMENT -> new OrderSpecifier<?>[]{
                    new OrderSpecifier<>(Order.DESC, study.statistics.commentCount),
                    new OrderSpecifier<>(Order.DESC, study.id),
            };
        };
    }
}
