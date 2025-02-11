package com.jj.swm.domain.study.core.repository.custom;

import com.jj.swm.domain.study.core.dto.StudyInquiryCondition;
import com.jj.swm.domain.study.core.entity.Study;

import java.util.List;

public interface CustomStudyRepository {

    List<Study> findPagedStudiesByCondition(int pageSize, StudyInquiryCondition inquiryCondition);
}
