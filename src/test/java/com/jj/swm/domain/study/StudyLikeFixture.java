package com.jj.swm.domain.study;

import com.jj.swm.domain.study.core.entity.Study;
import com.jj.swm.domain.study.core.entity.StudyLike;
import com.jj.swm.domain.user.entity.User;

public class StudyLikeFixture {

    public static StudyLike createStudyLike(User user, Study study) {
        return StudyLike.builder()
                .user(user)
                .study(study)
                .build();
    }
}

