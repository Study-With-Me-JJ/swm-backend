package com.jj.swm.domain.study.repository;

import com.jj.swm.domain.study.dto.QStudyBookmarkInfo;
import com.jj.swm.domain.study.dto.StudyBookmarkInfo;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

import static com.jj.swm.domain.study.entity.QStudy.study;
import static com.jj.swm.domain.study.entity.QStudyBookmark.studyBookmark;
import static com.jj.swm.domain.user.core.entity.QUser.user;

@RequiredArgsConstructor
public class CustomStudyBookmarkRepositoryImpl implements CustomStudyBookmarkRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public List<StudyBookmarkInfo> findAllByUserIdAndStudyIds(UUID userId, List<Long> studyIds) {
        return jpaQueryFactory.select(new QStudyBookmarkInfo(study.id, studyBookmark.id))
                .from(studyBookmark)
                .join(studyBookmark.study, study)
                .join(studyBookmark.user, user)
                .where(user.id.eq(userId), study.id.in(studyIds))
                .fetch();
    }
}
