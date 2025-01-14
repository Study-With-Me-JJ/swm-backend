package com.jj.swm.domain.studyroom.repository.jdbc;

import com.jj.swm.domain.studyroom.entity.StudyRoom;
import com.jj.swm.domain.studyroom.entity.StudyRoomType;

import java.time.DayOfWeek;
import java.util.List;

public interface JdbcDayOffRepository {
    void batchInsert(List<DayOfWeek> dayOffs, StudyRoom studyRoom);
}
