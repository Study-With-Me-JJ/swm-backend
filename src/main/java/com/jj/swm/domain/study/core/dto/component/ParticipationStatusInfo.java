package com.jj.swm.domain.study.core.dto.component;

import com.jj.swm.domain.study.core.entity.StudyRecruitmentPosition.RecruitmentPositionTitle;
import com.jj.swm.domain.study.participation.entity.StudyParticipation;
import com.jj.swm.domain.study.participation.entity.StudyParticipationStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ParticipationStatusInfo {

    private Long participationId;

    private StudyParticipationStatus status;

    private RecruitmentPositionTitle title;

    public static ParticipationStatusInfo from(StudyParticipation participation) {
        return ParticipationStatusInfo.builder()
                .participationId(participation.getId())
                .status(participation.getStatus())
                .title(participation.getRecruitmentPosition().getTitle())
                .build();
    }

    public static ParticipationStatusInfo empty() {
        return ParticipationStatusInfo.builder()
                .participationId(null)
                .status(null)
                .title(null)
                .build();
    }
}
