package com.jj.swm.domain.studyroom.core.repository.jdbc;

import com.jj.swm.domain.studyroom.core.entity.StudyRoom;

import java.util.List;

public interface JdbcTagRepository {
    void batchInsert(List<String> tags, StudyRoom studyRoom);
}
