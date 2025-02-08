package com.jj.swm.domain.study;

import com.jj.swm.domain.study.entity.core.Study;
import com.jj.swm.domain.study.entity.core.StudyCategory;
import com.jj.swm.domain.study.entity.core.StudyStatus;
import com.jj.swm.domain.user.entity.User;

public class StudyFixture {

    public static final Long studyId = -1L;

    public static Study createStudy(User user) {
        return Study.builder()
                .title("test_title")
                .content("test_content")
                .category(StudyCategory.ALGORITHM)
                .likeCount(0)
                .commentCount(0)
                .status(StudyStatus.ACTIVE)
                .viewCount(0)
                .user(user)
                .build();
    }
}
