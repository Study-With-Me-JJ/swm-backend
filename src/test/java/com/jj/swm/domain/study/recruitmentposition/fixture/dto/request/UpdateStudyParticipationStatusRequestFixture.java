package com.jj.swm.domain.study.recruitmentposition.fixture.dto.request;

import com.jj.swm.domain.study.recruitmentposition.dto.request.UpdateStudyParticipationStatusRequest;
import com.jj.swm.domain.study.recruitmentposition.entity.StudyParticipationStatus;

public class UpdateStudyParticipationStatusRequestFixture {

    public static UpdateStudyParticipationStatusRequest create(StudyParticipationStatus status) {
        return UpdateStudyParticipationStatusRequest.builder()
                .status(status)
                .build();
    }
}
