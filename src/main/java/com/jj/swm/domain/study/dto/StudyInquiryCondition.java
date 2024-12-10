package com.jj.swm.domain.study.dto;

import com.jj.swm.domain.study.entity.StudyCategory;
import com.jj.swm.domain.study.entity.StudyStatus;
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
