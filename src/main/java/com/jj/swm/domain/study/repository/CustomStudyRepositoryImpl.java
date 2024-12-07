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
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.function.Supplier;

import static com.jj.swm.domain.study.entity.QStudy.study;


@RequiredArgsConstructor
public class CustomStudyRepositoryImpl implements CustomStudyRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Study> findAllWithUserAndTags(Pageable pageable, StudyInquiryCondition inquiryConditionRequest) {
        OrderSpecifier<?> orderSpecifier = createOrderSpecifier(inquiryConditionRequest.getSortCriteria());

        List<Long> ids = jpaQueryFactory.select(study.id)
                .from(study)
                .where(
                        studyCategoryEq(inquiryConditionRequest.getCategory()),
                        studyStatusEq(inquiryConditionRequest.getStatus())
                )
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return jpaQueryFactory.selectFrom(study)
                .join(study.user)
                .fetchJoin()
                .leftJoin(study.studyTags)
                .fetchJoin()
                .where(study.id.in(ids))
                .orderBy(orderSpecifier)
                .distinct()
                .fetch();
    }

    @Override
    public Long countTotal(StudyInquiryCondition inquiryConditionRequest) {
        return jpaQueryFactory.select(study.count())
                .from(study)
                .where(
                        studyCategoryEq(inquiryConditionRequest.getCategory()),
                        studyStatusEq(inquiryConditionRequest.getStatus())
                )
                .fetchOne();
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
        } catch (IllegalArgumentException e) {
            return new BooleanBuilder();
        }
    }

    private OrderSpecifier<?> createOrderSpecifier(SortCriteria sortCriteria) {
        return switch (sortCriteria != null ? sortCriteria : SortCriteria.NEWEST) {
            case SortCriteria.LIKE -> new OrderSpecifier<>(Order.DESC, study.likeCount);
            case SortCriteria.OLDEST -> new OrderSpecifier<>(Order.ASC, study.id);
            case SortCriteria.NEWEST -> new OrderSpecifier<>(Order.DESC, study.id);
        };
    }
}
