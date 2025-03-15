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

@RequiredArgsConstructor
public class CustomStudyBookmarkRepositoryImpl implements CustomStudyBookmarkRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public List<StudyBookmarkInfo> findAllByUserIdAndStudyIds(UUID userId, List<Long> studyIds) {
        return jpaQueryFactory.select(new QStudyBookmarkInfo(studyBookmark.id, study.id))
                .from(studyBookmark)
                .where(studyBookmark.user.id.eq(userId), studyBookmark.study.id.in(studyIds))
                .fetch();
    }
}
