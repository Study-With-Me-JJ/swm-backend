package com.jj.swm.domain.studyroom.repository.jdbc;

import com.jj.swm.domain.studyroom.entity.StudyRoom;
import com.jj.swm.domain.studyroom.entity.StudyRoomType;

import java.util.List;

public interface JdbcTypeInfoRepository {
    void batchInsert(List<StudyRoomType> types, StudyRoom studyRoom);
}
