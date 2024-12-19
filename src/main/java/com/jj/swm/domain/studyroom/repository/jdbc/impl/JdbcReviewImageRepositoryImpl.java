package com.jj.swm.domain.studyroom.repository.jdbc.impl;

import com.jj.swm.domain.studyroom.entity.StudyRoom;
import com.jj.swm.domain.studyroom.entity.StudyRoomReview;
import com.jj.swm.domain.studyroom.repository.jdbc.JdbcReviewImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@RequiredArgsConstructor
public class JdbcReviewImageRepositoryImpl implements JdbcReviewImageRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void batchInsert(List<String> imageUrls, StudyRoomReview studyRoomReview) {
        String sql = "insert into study_room_review_image (study_room_review_id, image_url) values (?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, studyRoomReview.getId());
                ps.setString(2, imageUrls.get(i));
            }

            @Override
            public int getBatchSize() {
                return imageUrls.size();
            }
        });
    }
}
