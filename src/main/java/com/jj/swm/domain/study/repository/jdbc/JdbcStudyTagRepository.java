package com.jj.swm.domain.study.repository.jdbc;

import com.jj.swm.domain.study.entity.core.Study;

import java.util.List;

public interface JdbcStudyTagRepository {

    void batchInsert(Study study, List<String> tags);
}
