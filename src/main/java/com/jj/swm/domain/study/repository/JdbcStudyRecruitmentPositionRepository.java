package com.jj.swm.domain.study.repository;

import com.jj.swm.domain.study.dto.request.StudyRecruitPositionsCreateRequest;
import com.jj.swm.domain.study.entity.Study;

import java.util.List;

public interface JdbcStudyRecruitmentPositionRepository {

    void batchInsert(Study study, List<StudyRecruitPositionsCreateRequest> createRequests);
}
