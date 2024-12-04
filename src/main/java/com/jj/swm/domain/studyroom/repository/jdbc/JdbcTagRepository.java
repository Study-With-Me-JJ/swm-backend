package com.jj.swm.domain.studyroom.repository.jdbc;

import com.jj.swm.domain.studyroom.entity.StudyRoom;
import com.jj.swm.domain.studyroom.entity.StudyRoomType;

import java.util.List;

public interface JdbcTagRepository {
    void batchInsert(List<String> tags, StudyRoom studyRoom);
}
