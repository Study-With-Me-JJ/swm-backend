package com.jj.swm.domain.studyroom.core.repository.jdbc.impl;

import com.jj.swm.domain.studyroom.core.entity.StudyRoom;
import com.jj.swm.domain.studyroom.core.entity.StudyRoomType;
import com.jj.swm.domain.studyroom.core.repository.jdbc.JdbcTypeInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

@RequiredArgsConstructor
public class JdbcTypeInfoRepositoryImpl implements JdbcTypeInfoRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void batchInsert(List<StudyRoomType> types, StudyRoom studyRoom) {
        String sql = "insert into study_room_type_info (study_room_id, type) values (?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, studyRoom.getId());
                ps.setObject(2, types.get(i).name(), Types.OTHER);
            }

            @Override
            public int getBatchSize() {
                return types.size();
            }
        });
    }
}
