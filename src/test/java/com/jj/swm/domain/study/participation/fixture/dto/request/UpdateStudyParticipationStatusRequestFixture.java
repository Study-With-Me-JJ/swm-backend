package com.jj.swm.domain.study.participation.fixture.dto.request;

import com.jj.swm.domain.study.participation.dto.request.UpdateStudyParticipationStatusRequest;
import com.jj.swm.domain.study.participation.entity.StudyParticipation.StudyParticipationStatus;

public class UpdateStudyParticipationStatusRequestFixture {

    public static UpdateStudyParticipationStatusRequest create(StudyParticipationStatus status) {
        return UpdateStudyParticipationStatusRequest.builder()
                .status(status)
                .build();
    }
}
