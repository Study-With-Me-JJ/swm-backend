package com.jj.swm.domain.study.repository.jdbc;

import com.jj.swm.domain.study.dto.recruitmentposition.request.RecruitPositionCreateRequest;
import com.jj.swm.domain.study.entity.core.Study;

import java.util.List;

public interface JdbcRecruitmentPositionRepository {

    void batchInsert(Study study, List<RecruitPositionCreateRequest> createRequests);
}
