package com.jj.swm.domain.study.repository;

import com.jj.swm.domain.study.dto.request.RecruitPositionUpsertRequest;
import com.jj.swm.domain.study.entity.Study;

import java.util.List;

public interface JdbcRecruitmentPositionRepository {

    void batchInsert(Study study, List<RecruitPositionUpsertRequest> createRequests);
}
