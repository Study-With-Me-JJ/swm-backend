package com.jj.swm.domain.study.core.dto.component;

import com.jj.swm.domain.study.core.entity.StudyRecruitmentPosition.RecruitmentPositionTitle;
import com.jj.swm.domain.study.participation.entity.StudyParticipation;
import com.jj.swm.domain.study.participation.entity.StudyParticipationStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ParticipationStatusInfo {

    Long participationId;

    StudyParticipationStatus status;

    RecruitmentPositionTitle title;

    public static ParticipationStatusInfo from(StudyParticipation participation) {
        return ParticipationStatusInfo.builder()
                .participationId(participation.getId())
                .status(participation.getStatus())
                .title(participation.getRecruitmentPosition().getTitle())
                .build();
    }
}
