package com.jj.swm.domain.study.dto.core;

import com.jj.swm.domain.study.entity.core.StudyCategory;
import com.jj.swm.domain.study.entity.core.StudyStatus;
import com.jj.swm.domain.study.enums.SortCriteria;
import lombok.*;

@Getter
@Setter
public class StudyInquiryCondition {

    private StudyCategory category;

    private StudyStatus status;

    private Long lastStudyId;

    private SortCriteria sortCriteria = SortCriteria.NEWEST;

    private Integer lastSortValue;
}
