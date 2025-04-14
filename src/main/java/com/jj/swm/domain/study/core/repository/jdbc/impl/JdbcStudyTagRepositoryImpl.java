package com.jj.swm.domain.study.core.repository.jdbc.impl;

import com.jj.swm.domain.study.core.entity.Study;
import com.jj.swm.domain.study.core.repository.jdbc.JdbcStudyTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class JdbcStudyTagRepositoryImpl implements JdbcStudyTagRepository {

    private final JdbcTemplate jdbcTemplate;

    public void batchInsert(List<String> tags, Study study) {
        String sql = "insert into study_tag(name, study_id) VALUES(?,?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                String tag = tags.get(i);

                ps.setString(1, tag);
                ps.setLong(2, study.getId());
            }

            @Override
            public int getBatchSize() {
                return tags.size();
            }
        });
    }
}
