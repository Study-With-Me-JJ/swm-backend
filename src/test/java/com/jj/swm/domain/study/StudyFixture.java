package com.jj.swm.domain.study;

import com.jj.swm.domain.study.entity.Study;
import com.jj.swm.domain.study.entity.StudyCategory;
import com.jj.swm.domain.study.entity.StudyStatus;
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
                .thumbnail("test_thumbnail")
                .user(user)
                .build();
    }
}
