package com.jj.swm.domain.studyroom.core.repository.jdbc;

import com.jj.swm.domain.studyroom.core.entity.StudyRoom;
import com.jj.swm.domain.studyroom.core.entity.StudyRoomOption;

import java.util.List;

public interface JdbcOptionInfoRepository {
    void batchInsert(List<StudyRoomOption> options, StudyRoom studyRoom);
}
