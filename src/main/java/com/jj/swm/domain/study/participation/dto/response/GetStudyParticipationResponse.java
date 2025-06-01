package com.jj.swm.domain.study.participation.dto.response;

import com.jj.swm.domain.study.participation.entity.StudyParticipation;
import com.jj.swm.domain.study.participation.entity.StudyParticipation.StudyParticipationStatus;
import com.jj.swm.domain.user.core.dto.component.UserInfo;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetStudyParticipationResponse {

    private Long participationId;

    private StudyParticipationStatus status;

    private String coverLetter;

    private UserInfo userinfo;

    public static GetStudyParticipationResponse from(StudyParticipation participation) {
        return GetStudyParticipationResponse.builder()
                .participationId(participation.getId())
                .status(participation.getStatus())
                .coverLetter(participation.getCoverLetter())
                .userinfo(UserInfo.from(participation.getUser()))
                .build();
    }
}
