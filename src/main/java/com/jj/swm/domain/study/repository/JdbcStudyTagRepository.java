package com.jj.swm.domain.study.repository;

import com.jj.swm.domain.study.entity.Study;

import java.util.List;

public interface JdbcStudyTagRepository {

    void batchInsert(Study study, List<String> tags);
}
