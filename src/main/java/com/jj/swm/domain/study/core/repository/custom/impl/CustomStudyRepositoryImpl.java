package com.jj.swm.domain.study.core.repository.custom.impl;

import com.jj.swm.domain.study.core.dto.GetStudyCondition;
import com.jj.swm.domain.study.core.dto.SortCriteria;
import com.jj.swm.domain.study.core.entity.RecruitmentPositionTitle;
import com.jj.swm.domain.study.core.entity.Study;
import com.jj.swm.domain.study.core.entity.StudyCategory;
import com.jj.swm.domain.study.core.entity.StudyStatus;
import com.jj.swm.domain.study.core.repository.custom.CustomStudyRepository;
import com.jj.swm.global.common.util.ListCheckUtils;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.jj.swm.domain.common.utils.QueryDSLBooleanUtils.nullSafeBuilder;
import static com.jj.swm.domain.study.core.entity.QStudy.study;


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
        return nullSafeBuilder(() -> study.title.contains(title));
    }

    private BooleanBuilder studyCategoryEq(StudyCategory category) {
        return nullSafeBuilder(() -> study.category.eq(category));
    }

    private BooleanBuilder studyStatusEq(StudyStatus status) {
        return nullSafeBuilder(() -> study.status.eq(status));
    }

    private BooleanBuilder recruitmentPositionTitleExists(List<RecruitmentPositionTitle> titles) {
        return ListCheckUtils.isListPresent(titles)
                ? nullSafeBuilder(() -> study.studyRecruitmentPositions.any().title.in(titles))
                : null;
    }


    private BooleanBuilder buildSortPredicate(GetStudyCondition condition) {
        Integer lastSortValue = condition.getLastSortValue();
        Long lastId = condition.getLastStudyId();

        return switch (condition.getSortCriteria()) {
            case LIKE -> nullSafeBuilder(() -> study.likeCount.lt(lastSortValue)
                    .or(study.likeCount.eq(lastSortValue).and(study.id.lt(lastId))));
            case COMMENT -> nullSafeBuilder(() -> study.commentCount.lt(lastSortValue)
                    .or(study.commentCount.eq(lastSortValue).and(study.id.lt(lastId))));
            default -> nullSafeBuilder(() -> study.id.lt(lastId));
        };
    }

    private OrderSpecifier<?>[] buildOrderSpecifier(SortCriteria sortCriteria) {
        return switch (sortCriteria) {
            case SortCriteria.LIKE -> new OrderSpecifier<?>[]{
                    new OrderSpecifier<>(Order.DESC, study.likeCount),
                    new OrderSpecifier<>(Order.DESC, study.id),
            };
            case SortCriteria.NEWEST -> new OrderSpecifier<?>[]{
                    new OrderSpecifier<>(Order.DESC, study.id)
            };
            case SortCriteria.COMMENT -> new OrderSpecifier<?>[]{
                    new OrderSpecifier<>(Order.DESC, study.commentCount),
                    new OrderSpecifier<>(Order.DESC, study.id),
            };
        };
    }
}
