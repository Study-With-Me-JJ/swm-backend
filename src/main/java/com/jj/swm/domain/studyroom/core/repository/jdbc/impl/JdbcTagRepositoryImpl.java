package com.jj.swm.domain.studyroom.core.repository.jdbc.impl;

import com.jj.swm.domain.studyroom.core.entity.StudyRoom;
import com.jj.swm.domain.studyroom.core.repository.jdbc.JdbcTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@RequiredArgsConstructor
public class JdbcTagRepositoryImpl implements JdbcTagRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void batchInsert(List<String> tags, StudyRoom studyRoom) {
        String sql = "insert into study_room_tag (study_room_id, tag) values (?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, studyRoom.getId());
                ps.setString(2, tags.get(i));
            }

            @Override
            public int getBatchSize() {
                return tags.size();
            }
        });
    }
}
