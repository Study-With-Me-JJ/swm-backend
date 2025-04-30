package com.jj.swm.domain.study.participation.fixture.entity;

import com.jj.swm.domain.study.core.entity.Study;
import com.jj.swm.domain.study.core.entity.StudyRecruitmentPosition;
import com.jj.swm.domain.study.participation.entity.StudyParticipation;
import com.jj.swm.domain.user.core.entity.User;

import java.time.LocalDateTime;

import static com.jj.swm.domain.study.participation.entity.StudyParticipationStatus.REJECTED;

public class StudyParticipationFixture {

    public static StudyParticipation createForDeletedAtAfterThreeDaysSuccess(
            Study study,
            StudyRecruitmentPosition recruitmentPosition,
            User user
    ) {
        return StudyParticipation.builder()
                .study(study)
                .deletedAt(LocalDateTime.now().minusDays(3))
                .coverLetter("test_cover_letter")
                .kakaoId("test_kakaoId")
                .status(REJECTED)
                .recruitmentPosition(recruitmentPosition)
                .fileInfo(null)
                .user(user)
                .build();
    }
}
