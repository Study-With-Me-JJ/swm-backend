package com.jj.swm.domain.study.recruitmentposition.fixture.entity;

import com.jj.swm.domain.study.core.entity.Study;
import com.jj.swm.domain.study.recruitmentposition.entity.StudyParticipation;
import com.jj.swm.domain.study.recruitmentposition.entity.StudyParticipationStatus;
import com.jj.swm.domain.study.recruitmentposition.entity.StudyRecruitmentPosition;
import com.jj.swm.domain.user.core.entity.User;

import java.time.LocalDateTime;

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
                .status(StudyParticipationStatus.REJECTED)
                .recruitmentPosition(recruitmentPosition)
                .fileInfo(null)
                .user(user)
                .build();
    }
}
