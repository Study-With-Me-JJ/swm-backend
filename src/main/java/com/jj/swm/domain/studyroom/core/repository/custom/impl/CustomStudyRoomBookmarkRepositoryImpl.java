package com.jj.swm.domain.studyroom.core.repository.custom.impl;

import com.jj.swm.domain.studyroom.core.dto.QStudyRoomBookmarkInfo;
import com.jj.swm.domain.studyroom.core.dto.StudyRoomBookmarkInfo;
import com.jj.swm.domain.studyroom.core.repository.custom.CustomStudyRoomBookmarkRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

import static com.jj.swm.domain.studyroom.core.entity.QStudyRoom.studyRoom;
import static com.jj.swm.domain.studyroom.core.entity.QStudyRoomBookmark.studyRoomBookmark;
import static com.jj.swm.domain.user.entity.QUser.user;

@RequiredArgsConstructor
public class CustomStudyRoomBookmarkRepositoryImpl implements CustomStudyRoomBookmarkRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<StudyRoomBookmarkInfo> findAllByUserIdAndStudyRoomIds(UUID userId, List<Long> studyRoomIds) {
        return jpaQueryFactory.select(new QStudyRoomBookmarkInfo(studyRoomBookmark.id, studyRoom.id))
                .from(studyRoomBookmark)
                .join(studyRoomBookmark.studyRoom, studyRoom)
                .join(studyRoomBookmark.user, user)
                .where(user.id.eq(userId), studyRoom.id.in(studyRoomIds))
                .fetch();
    }
}
