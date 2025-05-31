package com.jj.swm.domain.study.core.fixture.dto.request;

import com.jj.swm.domain.study.core.dto.request.UpdateStudyStatusRequest;

import static com.jj.swm.domain.study.core.entity.Study.StudyStatus.INACTIVE;

public class UpdateStudyStatusRequestFixture {

    public static UpdateStudyStatusRequest create() {
        return UpdateStudyStatusRequest.builder()
                .status(INACTIVE)
                .build();
    }
}
