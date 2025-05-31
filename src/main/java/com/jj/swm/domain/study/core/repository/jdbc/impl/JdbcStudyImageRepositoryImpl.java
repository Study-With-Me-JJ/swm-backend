package com.jj.swm.domain.study.core.repository.jdbc.impl;

import com.jj.swm.domain.study.core.entity.Study;
import com.jj.swm.domain.study.core.repository.jdbc.JdbcStudyImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@RequiredArgsConstructor
public class JdbcStudyImageRepositoryImpl implements JdbcStudyImageRepository {

    private final JdbcTemplate jdbcTemplate;

    public void batchInsert(List<String> imageUrls, Study study) {
        String sql = "insert into study_image(image_url, study_id) VALUES(?,?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                String imageUrl = imageUrls.get(i);

                ps.setString(1, imageUrl);
                ps.setLong(2, study.getId());
            }

            @Override
            public int getBatchSize() {
                return imageUrls.size();
            }
        });
    }
}
