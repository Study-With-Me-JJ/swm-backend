package com.jj.swm.domain.study.recruitmentposition.repository.jdbc;

import com.jj.swm.domain.study.core.entity.Study;
import com.jj.swm.domain.study.recruitmentposition.dto.request.UpsertRecruitmentPositionRequest;

import java.util.List;

public interface JdbcRecruitmentPositionRepository {

    void batchInsert(List<UpsertRecruitmentPositionRequest> requests, Study study);
}
