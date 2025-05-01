package com.jj.swm.domain.study.core.repository.custom;

import com.jj.swm.domain.study.core.dto.GetStudyCondition;
import com.jj.swm.domain.study.core.entity.Study;

import java.util.List;

public interface CustomStudyRepository {

    List<Study> findPagedStudyByCondition(GetStudyCondition condition, int pageSize);
}
