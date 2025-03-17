package com.jj.swm.domain.study.core.fixture.dto.request;

import com.jj.swm.domain.study.core.dto.request.UpdateStudyStatusRequest;
import com.jj.swm.domain.study.core.entity.StudyStatus;

public class UpdateStudyStatusRequestFixture {

    public static UpdateStudyStatusRequest create() {
        return UpdateStudyStatusRequest.builder()
                .status(StudyStatus.INACTIVE)
                .build();
    }
}
