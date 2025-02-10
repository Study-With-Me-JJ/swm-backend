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
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.function.Supplier;

import static com.jj.swm.domain.studyroom.entity.QStudyRoom.studyRoom;

@RequiredArgsConstructor
public class CustomStudyRoomRepositoryImpl implements CustomStudyRoomRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private static final double earthRadiusKm = 6371.0;

    @Override
    public List<StudyRoom> findAllWithPaginationAndCondition(int pageSize, GetStudyRoomCondition condition) {
        return jpaQueryFactory.selectFrom(studyRoom)
                .where(
                        studyRoomTitleContains(condition.getTitle()),
                        studyRoomHeadcountGoe(condition.getHeadCount()),
                        studyRoomPriceBetween(condition.getMinPricePerHour(), condition.getMaxPricePerHour()),
                        studyRoomLocalityExists(condition.getLocality()),
                        studyRoomOptionsContains(condition.getOptions()),
                        createSortPredicate(condition)
                )
                .orderBy(createOrderSpecifier(condition))
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

    private BooleanBuilder studyRoomLocalityExists(String locality) {
        return this.nullSafeBuilder(() -> studyRoom.address.locality.eq(locality));
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
            case STAR -> this.nullSafeBuilder(() -> studyRoom.averageRating.lt(lastAverageRating)
                    .or(studyRoom.averageRating.eq(lastAverageRating).and(studyRoom.id.lt(lastStudyRoomId))));
            case LIKE -> this.nullSafeBuilder(() -> studyRoom.likeCount.lt(lastSortValue)
                    .or(studyRoom.likeCount.eq(lastSortValue).and(studyRoom.id.lt(lastStudyRoomId))));
            case REVIEW -> this.nullSafeBuilder(() -> studyRoom.reviewCount.lt(lastSortValue)
                    .or(studyRoom.reviewCount.eq(lastSortValue).and(studyRoom.id.lt(lastStudyRoomId))));
            case PRICE_ASC -> this.nullSafeBuilder(() -> studyRoom.entireMinPricePerHour.gt(lastSortValue)
                    .or(studyRoom.entireMinPricePerHour.eq(lastSortValue).and(studyRoom.id.lt(lastStudyRoomId))));
            case PRICE_DESC -> this.nullSafeBuilder(() -> studyRoom.entireMaxPricePerHour.lt(lastSortValue)
                    .or(studyRoom.entireMaxPricePerHour.eq(lastSortValue).and(studyRoom.id.lt(lastStudyRoomId))));
            case DISTANCE -> {
                NumberExpression<Double> distanceExpression = calculateDistance(
                        condition.getUserLatitude(), condition.getUserLongitude()
                );

                if(distanceExpression == null) yield null;

                Double lastLatitudeValue = condition.getLastLatitudeValue();
                Double lastLongitudeValue = condition.getLastLongitudeValue();

                BooleanBuilder builder = new BooleanBuilder();
                if (lastLatitudeValue != null && lastLongitudeValue != null) {
                    NumberExpression<Double> lastDistanceExpression
                            = getLastDistanceExpression(condition, lastLatitudeValue, lastLongitudeValue);

                    builder.and(distanceExpression.gt(lastDistanceExpression)
                            .or(distanceExpression.eq(lastDistanceExpression).and(studyRoom.id.lt(lastStudyRoomId))));
                }

                yield builder;
            }
        };
    }

    private OrderSpecifier<?>[] createOrderSpecifier(GetStudyRoomCondition condition) {
        SortCriteria sortCriteria = condition.getSortCriteria();

        Double userLatitude = condition.getUserLatitude();
        Double userLongitude = condition.getUserLongitude();

        if (sortCriteria == SortCriteria.DISTANCE && userLatitude != null && userLongitude != null) {
            NumberExpression<Double> distanceExpression
                    = calculateDistance(userLatitude, userLongitude);

            return new OrderSpecifier<?>[]{
                    new OrderSpecifier<>(Order.ASC, distanceExpression),
                    new OrderSpecifier<>(Order.DESC, studyRoom.id)
            };
        }

        return switch (sortCriteria) {
            case STAR -> new OrderSpecifier<?>[]{
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
            case PRICE_ASC -> new OrderSpecifier<?>[]{
                    new OrderSpecifier<>(Order.ASC, studyRoom.entireMinPricePerHour),
                    new OrderSpecifier<>(Order.DESC, studyRoom.id),
            };
            case PRICE_DESC -> new OrderSpecifier<?>[]{
                    new OrderSpecifier<>(Order.DESC, studyRoom.entireMaxPricePerHour),
                    new OrderSpecifier<>(Order.DESC, studyRoom.id),
            };
            default -> new OrderSpecifier<?>[]{
                    new OrderSpecifier<>(Order.DESC, studyRoom.id)
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

    private NumberExpression<Double> calculateDistance(Double latitude, Double longitude) {
        if (latitude == null && longitude == null)
            return null;

        return Expressions.numberTemplate(Double.class,
                "({0} * acos(" +
                        "cos(radians({1})) * cos(radians({2})) * " +
                        "cos(radians({3}) - radians({4})) + " +
                        "sin(radians({1})) * sin(radians({2}))" +
                        "))",
                earthRadiusKm,
                latitude, studyRoom.coordinates.latitude,
                longitude, studyRoom.coordinates.longitude
        );
    }

    private NumberExpression<Double> getLastDistanceExpression(GetStudyRoomCondition condition, Double lastLatitudeValue, Double lastLongitudeValue) {
        return Expressions.numberTemplate(Double.class,
                "({0} * acos(" +
                        "cos(radians({1})) * cos(radians({2})) * " +
                        "cos(radians({3}) - radians({4})) + " +
                        "sin(radians({1})) * sin(radians({2}))" +
                        "))",
                earthRadiusKm,
                condition.getUserLatitude(), lastLatitudeValue,
                condition.getUserLongitude(), lastLongitudeValue
        );
    }
}
