package com.jj.swm.domain.study.participation.repository.jdbc.impl;

import com.jj.swm.domain.study.participation.entity.StudyParticipation;
import com.jj.swm.domain.study.participation.repository.jdbc.JdbcStudyParticipationLinkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class JdbcStudyParticipationLinkRepositoryImpl implements JdbcStudyParticipationLinkRepository {

    private final JdbcTemplate jdbcTemplate;

    public void batchInsert(List<String> links, StudyParticipation participation) {
        String sql = "insert into study_participation_link(study_participation_id, link) VALUES(?,?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                String link = links.get(i);

                ps.setLong(1, participation.getId());
                ps.setString(2, link);
            }

            @Override
            public int getBatchSize() {
                return links.size();
            }
        });
    }
}
