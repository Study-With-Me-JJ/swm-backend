package com.jj.swm.domain.study.repository;

import com.jj.swm.domain.study.dto.StudyInquiryCondition;
import com.jj.swm.domain.study.entity.Study;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomStudyRepository {

    List<Study> findAllWithUserAndTags(Pageable pageable, StudyInquiryCondition inquiryConditionRequest);

    Long countTotal(StudyInquiryCondition inquiryConditionRequest);
}
