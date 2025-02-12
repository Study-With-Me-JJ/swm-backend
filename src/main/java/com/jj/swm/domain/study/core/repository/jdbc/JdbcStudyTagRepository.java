package com.jj.swm.domain.study.core.repository.jdbc;

import com.jj.swm.domain.study.core.entity.Study;

import java.util.List;

public interface JdbcStudyTagRepository {

    void batchInsert(Study study, List<String> tagList);
}
