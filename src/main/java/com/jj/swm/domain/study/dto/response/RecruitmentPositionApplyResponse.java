package com.jj.swm.domain.study.dto.response;

import com.jj.swm.domain.study.entity.StudyParticipant;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecruitmentPositionApplyResponse {

    private Long participantId;

    public static RecruitmentPositionApplyResponse from(StudyParticipant studyParticipant) {
        return RecruitmentPositionApplyResponse.builder()
                .participantId(studyParticipant.getId())
                .build();
    }
}
