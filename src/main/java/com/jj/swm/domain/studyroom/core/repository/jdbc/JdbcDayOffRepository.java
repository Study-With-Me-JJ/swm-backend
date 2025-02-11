package com.jj.swm.domain.studyroom.core.repository.jdbc;

import com.jj.swm.domain.studyroom.core.entity.StudyRoom;

import java.time.DayOfWeek;
import java.util.List;

public interface JdbcDayOffRepository {
    void batchInsert(List<DayOfWeek> dayOffs, StudyRoom studyRoom);
}
