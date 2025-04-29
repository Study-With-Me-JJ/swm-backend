package com.jj.swm.domain.studyroom.reservation.repository.custom.impl;

import com.jj.swm.domain.studyroom.reservation.dto.response.GetOwnerReservationInfoResponse;
import com.jj.swm.domain.studyroom.reservation.dto.response.GetReserverReservationInfoResponse;
import com.jj.swm.domain.studyroom.reservation.dto.response.QGetOwnerReservationInfoResponse;
import com.jj.swm.domain.studyroom.reservation.dto.response.QGetReserverReservationInfoResponse;
import com.jj.swm.domain.studyroom.reservation.entity.ApprovalStatus;
import com.jj.swm.domain.studyroom.reservation.repository.custom.CustomReservationInfoRepository;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;
import java.util.UUID;

import static com.jj.swm.domain.studyroom.core.entity.QStudyRoom.studyRoom;
import static com.jj.swm.domain.studyroom.reservation.entity.QStudyRoomReservationInfo.studyRoomReservationInfo;

@RequiredArgsConstructor
public class CustomReservationInfoRepositoryImpl implements CustomReservationInfoRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<GetOwnerReservationInfoResponse> findPagedReservationInfoByStudyRoomIdsAndStatus(
            List<Long> studyRoomIds,
            ApprovalStatus status,
            Pageable pageable
    ) {
        List<GetOwnerReservationInfoResponse> responses = jpaQueryFactory.select(
                new QGetOwnerReservationInfoResponse(
                        studyRoomReservationInfo.id,
                        studyRoomReservationInfo.reserverName,
                        studyRoomReservationInfo.reserverPhoneNumber,
                        studyRoomReservationInfo.checkInTime,
                        studyRoom.title,
                        studyRoomReservationInfo.approvalStatus
                )
        ).from(studyRoomReservationInfo)
         .leftJoin(studyRoomReservationInfo.studyRoom, studyRoom)
         .where(
                studyRoom.id.in(studyRoomIds),
                status == null ? null : studyRoomReservationInfo.approvalStatus.eq(status)
        ).orderBy(studyRoomReservationInfo.id.desc())
         .offset(pageable.getOffset())
         .limit(pageable.getPageSize())
         .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory.select(studyRoomReservationInfo.count())
                .from(studyRoomReservationInfo)
                .leftJoin(studyRoomReservationInfo.studyRoom, studyRoom)
                .where(
                        studyRoom.id.in(studyRoomIds),
                        status == null ? null : studyRoomReservationInfo.approvalStatus.eq(status)
                );

        return PageableExecutionUtils.getPage(responses, pageable, countQuery::fetchOne);
    }

    @Override
    public Page<GetReserverReservationInfoResponse> findPagedReservationInfoByUserIdAndStatus(
            UUID userId,
            ApprovalStatus status,
            Pageable pageable
    ) {
        List<GetReserverReservationInfoResponse> responses = jpaQueryFactory.select(
                        new QGetReserverReservationInfoResponse(
                                studyRoomReservationInfo.id,
                                studyRoomReservationInfo.reserverName,
                                studyRoomReservationInfo.reserverPhoneNumber,
                                studyRoomReservationInfo.checkInTime,
                                studyRoom.title,
                                studyRoomReservationInfo.approvalStatus
                        )
                ).from(studyRoomReservationInfo)
                .leftJoin(studyRoomReservationInfo.studyRoom, studyRoom)
                .where(
                        studyRoomReservationInfo.user.id.eq(userId),
                        status == null ? null : studyRoomReservationInfo.approvalStatus.eq(status)
                ).orderBy(studyRoomReservationInfo.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory.select(studyRoomReservationInfo.count())
                .from(studyRoomReservationInfo)
                .leftJoin(studyRoomReservationInfo.studyRoom, studyRoom)
                .where(
                        studyRoomReservationInfo.user.id.eq(userId),
                        status == null ? null : studyRoomReservationInfo.approvalStatus.eq(status)
                );

        return PageableExecutionUtils.getPage(responses, pageable, countQuery::fetchOne);
    }
}
