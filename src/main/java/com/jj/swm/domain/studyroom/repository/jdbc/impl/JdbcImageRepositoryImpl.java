package com.jj.swm.domain.studyroom.repository.jdbc.impl;

import com.jj.swm.domain.studyroom.entity.StudyRoom;
import com.jj.swm.domain.studyroom.repository.jdbc.JdbcImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.util.List;

@RequiredArgsConstructor
public class JdbcImageRepositoryImpl implements JdbcImageRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void batchInsert(List<String> imageUrls, StudyRoom studyRoom) {
        String sql = "insert into study_room_image (study_room_id, image_url, sort_order) values (?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, studyRoom.getId());
                ps.setString(2, imageUrls.get(i));
                ps.setInt(3, i);
            }

            @Override
            public int getBatchSize() {
                return imageUrls.size();
            }
        });
    }
}
