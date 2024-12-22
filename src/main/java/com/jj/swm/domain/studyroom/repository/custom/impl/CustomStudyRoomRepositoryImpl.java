package com.jj.swm.domain.studyroom.repository.custom.impl;

import com.jj.swm.domain.studyroom.dto.GetStudyRoomCondition;
import com.jj.swm.domain.studyroom.dto.SortCriteria;
import com.jj.swm.domain.studyroom.entity.StudyRoom;
import com.jj.swm.domain.studyroom.entity.StudyRoomOption;
import com.jj.swm.domain.studyroom.repository.custom.CustomStudyRoomRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.function.Supplier;

import static com.jj.swm.domain.studyroom.entity.QStudyRoom.studyRoom;

@RequiredArgsConstructor
public class CustomStudyRoomRepositoryImpl implements CustomStudyRoomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<StudyRoom> findAllWithPaginationAndCondition(int pageSize, GetStudyRoomCondition condition) {
        return jpaQueryFactory.selectFrom(studyRoom)
                .where(
                        studyRoomTitleContains(condition.getTitle()),
                        studyRoomHeadcountGoe(condition.getHeadCount()),
                        studyRoomPriceBetween(condition.getMinPricePerHour(), condition.getMaxPricePerHour()),
                        studyRoomOptionsContains(condition.getOptions()),
                        createSortPredicate(condition)
                )
                .orderBy(createOrderSpecifier(condition.getSortCriteria()))
                .limit(pageSize)
                .fetch();
    }

    private BooleanBuilder studyRoomTitleContains(String title) {
        return this.nullSafeBuilder(() -> studyRoom.title.contains(title));
    }

    private BooleanBuilder studyRoomHeadcountGoe(int headCount) {
        return this.nullSafeBuilder(() -> studyRoom.entireMaxHeadcount.goe(headCount));
    }

    private BooleanBuilder studyRoomPriceBetween(int minPricePerHour, int maxPricePerHour) {
        return this.nullSafeBuilder(() -> studyRoom.entireMinPricePerHour.between(minPricePerHour, maxPricePerHour));
    }

    private BooleanBuilder studyRoomOptionsContains(List<StudyRoomOption> options) {
        return options == null || options.isEmpty()
                ? null
                : this.nullSafeBuilder(() -> studyRoom.optionInfos.any().option.in(options));
    }

    private BooleanBuilder createSortPredicate(GetStudyRoomCondition condition) {
        Integer lastSortValue = condition.getLastSortValue();
        Long lastStudyRoomId = condition.getLastStudyRoomId();
        SortCriteria sortCriteria = condition.getSortCriteria();
        Double lastAverageRating = condition.getLastAverageRatingValue();

        return switch (sortCriteria) {
            case STARS -> this.nullSafeBuilder(() -> studyRoom.averageRating.lt(lastAverageRating)
                    .or(studyRoom.averageRating.eq(lastAverageRating).and(studyRoom.id.lt(lastStudyRoomId))));
            case LIKE -> this.nullSafeBuilder(() -> studyRoom.likeCount.lt(lastSortValue)
                    .or(studyRoom.likeCount.eq(lastSortValue).and(studyRoom.id.lt(lastStudyRoomId))));
            case REVIEW -> this.nullSafeBuilder(() -> studyRoom.reviewCount.lt(lastSortValue)
                    .or(studyRoom.reviewCount.eq(lastSortValue).and(studyRoom.id.lt(lastStudyRoomId))));
            case PRICE -> this.nullSafeBuilder(() -> studyRoom.entireMinPricePerHour.gt(lastSortValue)
                    .or(studyRoom.entireMinPricePerHour.eq(lastSortValue).and(studyRoom.id.lt(lastStudyRoomId))));
            default -> this.nullSafeBuilder(() -> studyRoom.id.lt(lastStudyRoomId));
        };
    }

    private OrderSpecifier<?>[] createOrderSpecifier(SortCriteria sortCriteria) {
        return switch (sortCriteria) {
            case STARS -> new OrderSpecifier<?>[]{
                    new OrderSpecifier<>(Order.DESC, studyRoom.averageRating),
                    new OrderSpecifier<>(Order.DESC, studyRoom.id),
            };
            case LIKE -> new OrderSpecifier<?>[]{
                    new OrderSpecifier<>(Order.DESC, studyRoom.likeCount),
                    new OrderSpecifier<>(Order.DESC, studyRoom.id),
            };
            case REVIEW -> new OrderSpecifier<?>[]{
                    new OrderSpecifier<>(Order.DESC, studyRoom.reviewCount),
                    new OrderSpecifier<>(Order.DESC, studyRoom.id),
            };
            case PRICE -> new OrderSpecifier<?>[]{
                    new OrderSpecifier<>(Order.ASC, studyRoom.entireMinPricePerHour),
                    new OrderSpecifier<>(Order.DESC, studyRoom.id),
            };
        };
    }

    private BooleanBuilder nullSafeBuilder(Supplier<BooleanExpression> f) {
        try {
            return new BooleanBuilder(f.get());
        } catch (IllegalArgumentException | NullPointerException e) {
            return null;
        }
    }
}
