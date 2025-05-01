package com.jj.swm.domain.study.core.repository.jdbc.impl;

import com.jj.swm.domain.study.core.dto.request.CreateRecruitmentPositionRequest;
import com.jj.swm.domain.study.core.entity.Study;
import com.jj.swm.domain.study.core.repository.jdbc.JdbcRecruitmentPositionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class JdbcRecruitmentPositionRepositoryImpl implements JdbcRecruitmentPositionRepository {

    private final JdbcTemplate jdbcTemplate;

    public void batchInsert(List<CreateRecruitmentPositionRequest> requests, Study study) {
        String sql = "insert into study_recruitment_position(study_id, title, headcount) " +
                " VALUES(?,?,?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                CreateRecruitmentPositionRequest request = requests.get(i);

                ps.setLong(1, study.getId());
                ps.setObject(2, request.getTitle().name(), Types.OTHER);
                ps.setInt(3, request.getHeadcount());
            }

            @Override
            public int getBatchSize() {
                return requests.size();
            }
        });
    }
}
