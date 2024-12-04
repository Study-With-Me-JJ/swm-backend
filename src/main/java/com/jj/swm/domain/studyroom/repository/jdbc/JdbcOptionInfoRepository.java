package com.jj.swm.domain.studyroom.repository.jdbc;

import com.jj.swm.domain.studyroom.entity.StudyRoom;
import com.jj.swm.domain.studyroom.entity.StudyRoomOption;
import com.jj.swm.domain.studyroom.entity.StudyRoomType;

import java.util.List;

public interface JdbcOptionInfoRepository {
    void batchInsert(List<StudyRoomOption> options, StudyRoom studyRoom);
}
