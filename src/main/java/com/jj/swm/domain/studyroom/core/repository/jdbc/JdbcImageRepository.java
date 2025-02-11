package com.jj.swm.domain.studyroom.core.repository.jdbc;

import com.jj.swm.domain.studyroom.core.entity.StudyRoom;

import java.util.List;

public interface JdbcImageRepository {
    void batchInsert(List<String> imageUrls, StudyRoom studyRoom);
}
