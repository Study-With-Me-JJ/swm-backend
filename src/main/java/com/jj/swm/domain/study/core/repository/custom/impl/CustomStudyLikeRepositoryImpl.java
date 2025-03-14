package com.jj.swm.domain.study.core.repository.custom.impl;

import com.jj.swm.domain.study.core.dto.QStudyLikeInfo;
import com.jj.swm.domain.study.core.dto.StudyLikeInfo;
import com.jj.swm.domain.study.core.repository.custom.CustomStudyLikeRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

import static com.jj.swm.domain.study.core.entity.QStudy.study;
import static com.jj.swm.domain.study.core.entity.QStudyLike.studyLike;

@RequiredArgsConstructor
public class CustomStudyLikeRepositoryImpl implements CustomStudyLikeRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<StudyLikeInfo> findAllByUserIdAndStudyIds(UUID userId, List<Long> studyIds) {
        return jpaQueryFactory.select(new QStudyLikeInfo(studyLike.id, study.id))
                .from(studyLike)
                .where(studyLike.user.id.eq(userId), studyLike.study.id.in(studyIds))
                .fetch();
    }
}
