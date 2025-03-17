package com.jj.swm.domain.study.core.fixture.dto.request;

import com.jj.swm.domain.study.core.dto.request.DeleteStudiesRequest;

import java.util.List;

public class DeleteStudiesRequestFixture {

    public static DeleteStudiesRequest create(List<Long> studyIds) {
        return DeleteStudiesRequest.builder()
                .studyIds(studyIds)
                .build();
    }
}
