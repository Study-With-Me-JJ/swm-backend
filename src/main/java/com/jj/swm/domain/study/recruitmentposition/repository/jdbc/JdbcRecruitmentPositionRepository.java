package com.jj.swm.domain.study.recruitmentposition.repository.jdbc;

import com.jj.swm.domain.study.recruitmentposition.dto.request.AddRecruitmentPositionRequest;
import com.jj.swm.domain.study.core.entity.Study;

import java.util.List;

public interface JdbcRecruitmentPositionRepository {

    void batchInsert(Study study, List<AddRecruitmentPositionRequest> requestList);
}
