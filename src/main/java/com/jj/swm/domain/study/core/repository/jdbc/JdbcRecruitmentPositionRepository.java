package com.jj.swm.domain.study.core.repository.jdbc;

import com.jj.swm.domain.study.core.dto.component.CreateRecruitmentPositionInfo;
import com.jj.swm.domain.study.core.entity.Study;

import java.util.List;

public interface JdbcRecruitmentPositionRepository {

   void batchInsert(List<CreateRecruitmentPositionInfo> infos, Study study);
}
