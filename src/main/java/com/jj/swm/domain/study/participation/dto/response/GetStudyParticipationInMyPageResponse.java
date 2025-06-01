package com.jj.swm.domain.study.participation.dto.response;

import com.jj.swm.domain.study.core.entity.StudyRecruitmentPosition.RecruitmentPositionTitle;
import com.jj.swm.domain.study.participation.entity.StudyParticipation;
import com.jj.swm.domain.study.participation.entity.StudyParticipation.StudyParticipationStatus;
import com.jj.swm.domain.user.core.dto.component.UserInfo;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetStudyParticipationInMyPageResponse {

    private Long participationId;

    private StudyParticipationStatus status;

    private String coverLetter;

    private UserInfo userInfo;

    private RecruitmentPositionTitle title;

    public static GetStudyParticipationInMyPageResponse from(StudyParticipation participation) {
        return GetStudyParticipationInMyPageResponse.builder()
                .participationId(participation.getId())
                .status(participation.getStatus())
                .coverLetter(participation.getCoverLetter())
                .userInfo(UserInfo.from(participation.getUser()))
                .title(participation.getRecruitmentPosition().getTitle())
                .build();
    }
}
