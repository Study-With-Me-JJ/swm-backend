package com.jj.swm.domain.study.repository;

import com.jj.swm.domain.study.entity.Study;

import java.util.List;

public interface JdbcStudyImageRepository {

    void batchInsert(Study study, List<String> imageUrls);
}
