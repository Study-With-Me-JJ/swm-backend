package com.jj.swm.domain.study.core.repository.custom.impl;

import com.jj.swm.domain.study.core.dto.QStudyBookmarkInfo;
import com.jj.swm.domain.study.core.dto.StudyBookmarkInfo;
import com.jj.swm.domain.study.core.repository.custom.CustomStudyBookmarkRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

import static com.jj.swm.domain.study.core.entity.QStudy.study;
import static com.jj.swm.domain.study.core.entity.QStudyBookmark.studyBookmark;
import static com.jj.swm.domain.user.core.entity.QUser.user;

@RequiredArgsConstructor
public class CustomStudyBookmarkRepositoryImpl implements CustomStudyBookmarkRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public List<StudyBookmarkInfo> findAllByUserIdAndStudyIdList(UUID userId, List<Long> studyIdList) {
        return jpaQueryFactory.select(new QStudyBookmarkInfo(studyBookmark.id, study.id))
                .from(studyBookmark)
                .join(studyBookmark.study, study)
                .join(studyBookmark.user, user)
                .where(user.id.eq(userId), study.id.in(studyIdList))
                .fetch();
    }
}
