package com.jj.swm.domain.study.participation.fixture.dto.request;

import com.jj.swm.domain.study.participation.dto.request.GetStudyParticipationCondition;
import com.jj.swm.domain.study.participation.entity.StudyParticipation.StudyParticipationStatus;

public class GetStudyParticipationConditionFixture {

    public static GetStudyParticipationCondition create() {
        return new GetStudyParticipationCondition();
    }

    public static GetStudyParticipationCondition create(StudyParticipationStatus status) {
        GetStudyParticipationCondition getStudyParticipationCondition = new GetStudyParticipationCondition();
        getStudyParticipationCondition.setStatus(status);

        return getStudyParticipationCondition;
    }

    public static GetStudyParticipationCondition create(int pageNo) {
        GetStudyParticipationCondition getStudyParticipationCondition = new GetStudyParticipationCondition();
        getStudyParticipationCondition.setPageNo(pageNo);

        return getStudyParticipationCondition;
    }

    public static GetStudyParticipationCondition create(StudyParticipationStatus status, int pageNo) {
        GetStudyParticipationCondition getStudyParticipationCondition = new GetStudyParticipationCondition();
        getStudyParticipationCondition.setStatus(status);
        getStudyParticipationCondition.setPageNo(pageNo);

        return getStudyParticipationCondition;
    }
}
