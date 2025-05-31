package com.jj.swm.domain.study.core.fixture.dto.request;

import com.jj.swm.domain.study.core.dto.request.DeleteStudyRequest;

import java.util.List;

public class DeleteStudyRequestFixture {

    public static DeleteStudyRequest create(List<Long> studyIds) {
        return DeleteStudyRequest.builder()
                .studyIds(studyIds)
                .build();
    }
}
