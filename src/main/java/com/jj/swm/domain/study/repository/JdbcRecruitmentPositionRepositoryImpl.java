package com.jj.swm.domain.study.repository;

import com.jj.swm.domain.study.dto.request.RecruitPositionUpsertRequest;
import com.jj.swm.domain.study.entity.Study;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class JdbcRecruitmentPositionRepositoryImpl implements JdbcRecruitmentPositionRepository {

    private final JdbcTemplate jdbcTemplate;

    public void batchInsert(Study study, List<RecruitPositionUpsertRequest> createRequests) {
        String sql = "insert into study_recruitment_position(study_id, title, headcount) VALUES(?,?,?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                RecruitPositionUpsertRequest createRequest = createRequests.get(i);

                ps.setLong(1, study.getId());
                ps.setString(2, createRequest.getTitle());
                ps.setInt(3, createRequest.getHeadcount());
            }

            @Override
            public int getBatchSize() {
                return createRequests.size();
            }
        });
    }
}
