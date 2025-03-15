package com.jj.swm.domain.study.core.repository.jdbc;

import com.jj.swm.domain.study.core.entity.Study;

import java.util.List;

public interface JdbcStudyImageRepository {

    void batchInsert(List<String> imageUrls, Study study);
}
