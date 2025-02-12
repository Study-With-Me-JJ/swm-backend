package com.jj.swm.domain.study.core.dto;

import com.jj.swm.domain.study.core.entity.StudyCategory;
import com.jj.swm.domain.study.core.entity.StudyStatus;
import lombok.*;

@Getter
@Setter
public class FindStudyCondition {

    private StudyCategory category;

    private StudyStatus status;

    private Long lastStudyId;

    private SortCriteria sortCriteria = SortCriteria.NEWEST;

    private Integer lastSortValue;
}
