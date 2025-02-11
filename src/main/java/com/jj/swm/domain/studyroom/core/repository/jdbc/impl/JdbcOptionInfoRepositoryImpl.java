package com.jj.swm.domain.studyroom.core.repository.jdbc.impl;

import com.jj.swm.domain.studyroom.core.entity.StudyRoom;
import com.jj.swm.domain.studyroom.core.entity.StudyRoomOption;
import com.jj.swm.domain.studyroom.core.repository.jdbc.JdbcOptionInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

@RequiredArgsConstructor
public class JdbcOptionInfoRepositoryImpl implements JdbcOptionInfoRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void batchInsert(List<StudyRoomOption> options, StudyRoom studyRoom) {
        String sql = "insert into study_room_option_info (study_room_id, option) values (?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, studyRoom.getId());
                ps.setObject(2, options.get(i).name(), Types.OTHER);
            }

            @Override
            public int getBatchSize() {
                return options.size();
            }
        });
    }
}
