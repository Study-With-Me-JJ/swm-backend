package com.jj.swm.domain.study.core.fixture.entity;

import com.jj.swm.domain.study.core.entity.Study;
import com.jj.swm.domain.study.core.entity.Study.StudyStatistics;
import com.jj.swm.domain.user.core.entity.User;

import static com.jj.swm.domain.study.core.entity.Study.StudyCategory.ALGORITHM;
import static com.jj.swm.domain.study.core.entity.Study.StudyStatus.ACTIVE;

public class StudyFixture {

    public static Study createForNoRecruitmentPosition(User user) {
        return Study.builder()
                .title("test_title")
                .content("test_content")
                .openChatUrl("test_open_chat_url")
                .category(ALGORITHM)
                .status(ACTIVE)
                .statistics(StudyStatistics.builder()
                        .viewCount(0)
                        .likeCount(0)
                        .commentCount(0)
                        .build())
                .user(user)
                .build();
    }
}
