package com.jj.swm.domain.study.repository.core;

import com.jj.swm.domain.study.dto.core.StudyInquiryCondition;
import com.jj.swm.domain.study.entity.core.Study;

import java.util.List;

public interface CustomStudyRepository {

    List<Study> findPagedStudiesByCondition(int pageSize, StudyInquiryCondition inquiryCondition);
}
