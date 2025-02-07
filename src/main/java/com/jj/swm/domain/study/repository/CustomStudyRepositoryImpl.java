package com.jj.swm.domain.study.repository;

import com.jj.swm.domain.study.dto.StudyInquiryCondition;
import com.jj.swm.domain.study.entity.Study;
import com.jj.swm.domain.study.entity.StudyCategory;
import com.jj.swm.domain.study.entity.StudyStatus;
import com.jj.swm.domain.study.enums.SortCriteria;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.function.Supplier;

import static com.jj.swm.domain.study.entity.QStudy.study;


@RequiredArgsConstructor
public class CustomStudyRepositoryImpl implements CustomStudyRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Study> findPagedWithTags(int pageSize, StudyInquiryCondition inquiryCondition) {
        return jpaQueryFactory.selectFrom(study)
                .where(
                        studyCategoryEq(inquiryCondition.getCategory()),
                        studyStatusEq(inquiryCondition.getStatus()),
                        createSortPredicate(inquiryCondition)
                )
                .orderBy(createOrderSpecifier(inquiryCondition.getSortCriteria()))
                .limit(pageSize)
                .fetch();
    }

    private BooleanBuilder studyCategoryEq(StudyCategory category) {
        return this.nullSafeBuilder(() -> study.category.eq(category));
    }

    private BooleanBuilder studyStatusEq(StudyStatus status) {
        return this.nullSafeBuilder(() -> study.status.eq(status));
    }

    private BooleanBuilder nullSafeBuilder(Supplier<BooleanExpression> f) {
        try {
            return new BooleanBuilder(f.get());
        } catch (IllegalArgumentException | NullPointerException e) {
            return null;
        }
    }

    private OrderSpecifier<?>[] createOrderSpecifier(SortCriteria sortCriteria) {
        return switch (sortCriteria) {
            case SortCriteria.LIKE -> new OrderSpecifier<?>[]{
                    new OrderSpecifier<>(Order.DESC, study.likeCount),
                    new OrderSpecifier<>(Order.DESC, study.id),
            };
            case SortCriteria.NEWEST -> new OrderSpecifier<?>[]{
                    new OrderSpecifier<>(Order.DESC, study.id)
            };
        };
    }

    private BooleanBuilder createSortPredicate(StudyInquiryCondition inquiryCondition) {
        Integer lastSortValue = inquiryCondition.getLastSortValue();
        SortCriteria sortCriteria = inquiryCondition.getSortCriteria();
        Long lastStudyId = inquiryCondition.getLastStudyId();

        return switch (sortCriteria) {
            case LIKE -> this.nullSafeBuilder(() -> study.likeCount.lt(lastSortValue)
                    .or(study.likeCount.eq(lastSortValue).and(study.id.lt(lastStudyId))));
            default -> this.nullSafeBuilder(() -> study.id.lt(lastStudyId));
        };
    }
}
