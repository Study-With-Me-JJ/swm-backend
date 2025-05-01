package com.jj.swm.domain.study.core.dto.response;

import com.jj.swm.domain.study.core.entity.RecruitmentPositionTitle;
import com.jj.swm.domain.study.participation.entity.StudyParticipation;
import com.jj.swm.domain.study.participation.entity.StudyParticipationStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetStudyParticipationStatusResponse {

    Long participationId;

    StudyParticipationStatus status;

    RecruitmentPositionTitle title;

    public static GetStudyParticipationStatusResponse from(StudyParticipation participation) {
        return GetStudyParticipationStatusResponse.builder()
                .participationId(participation.getId())
                .status(participation.getStatus())
                .title(participation.getRecruitmentPosition().getTitle())
                .build();
    }
}
