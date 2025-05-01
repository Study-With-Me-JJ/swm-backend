package com.jj.swm.domain.studyroom.core.repository.jdbc.impl;

import com.jj.swm.domain.studyroom.core.entity.StudyRoom;
import com.jj.swm.domain.studyroom.core.repository.jdbc.JdbcDayOffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.time.DayOfWeek;
import java.util.List;

@RequiredArgsConstructor
public class JdbcDayOffRepositoryImpl implements JdbcDayOffRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void batchInsert(List<DayOfWeek> dayOffs, StudyRoom studyRoom) {
        String sql = "insert into study_room_day_off (study_room_id, day_of_week) values (?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, studyRoom.getId());
                ps.setObject(2, dayOffs.get(i).name(), Types.OTHER);
            }

            @Override
            public int getBatchSize() {
                return dayOffs.size();
            }
        });

    }
}
