package com.jj.swm.domain.studyroom.core.repository.custom.impl;

import com.jj.swm.domain.studyroom.core.dto.QStudyRoomLikeInfo;
import com.jj.swm.domain.studyroom.core.dto.StudyRoomLikeInfo;
import com.jj.swm.domain.studyroom.core.repository.custom.CustomStudyRoomLikeRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

import static com.jj.swm.domain.studyroom.core.entity.QStudyRoom.studyRoom;
import static com.jj.swm.domain.studyroom.core.entity.QStudyRoomLike.studyRoomLike;
import static com.jj.swm.domain.user.core.entity.QUser.user;

@RequiredArgsConstructor
public class CustomStudyRoomLikeRepositoryImpl implements CustomStudyRoomLikeRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<StudyRoomLikeInfo> findAllByUserIdAndStudyRoomIds(UUID userId, List<Long> studyRoomIds) {
        return jpaQueryFactory.select(new QStudyRoomLikeInfo(studyRoomLike.id, studyRoom.id))
                .from(studyRoomLike)
                .join(studyRoomLike.studyRoom, studyRoom)
                .join(studyRoomLike.user, user)
                .where(user.id.eq(userId), studyRoom.id.in(studyRoomIds))
                .fetch();
    }
}
