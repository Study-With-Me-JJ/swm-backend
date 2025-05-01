package com.jj.swm.domain.studyroom.core.repository.jdbc;

import com.jj.swm.domain.studyroom.core.entity.StudyRoom;
import com.jj.swm.domain.studyroom.core.entity.StudyRoomType;

import java.util.List;

public interface JdbcTypeInfoRepository {
    void batchInsert(List<StudyRoomType> types, StudyRoom studyRoom);
}
