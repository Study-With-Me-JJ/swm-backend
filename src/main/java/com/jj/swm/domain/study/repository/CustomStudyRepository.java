package com.jj.swm.domain.study.repository;

import com.jj.swm.domain.study.dto.StudyInquiryCondition;
import com.jj.swm.domain.study.entity.Study;

import java.util.List;

public interface CustomStudyRepository {

    List<Study> findPagedWithTags(int pageSize, StudyInquiryCondition inquiryCondition);
}
