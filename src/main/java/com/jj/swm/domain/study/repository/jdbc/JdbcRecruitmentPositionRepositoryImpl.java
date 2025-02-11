package com.jj.swm.domain.study.repository.jdbc;

import com.jj.swm.domain.study.dto.recruitmentposition.request.RecruitPositionCreateRequest;
import com.jj.swm.domain.study.entity.core.Study;
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

    public void batchInsert(Study study, List<RecruitPositionCreateRequest> createRequests) {
        String sql = "insert into study_recruitment_position(study_id, title, headcount, accepted_count) " +
                " VALUES(?,?,?,?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                RecruitPositionCreateRequest createRequest = createRequests.get(i);

                ps.setLong(1, study.getId());
                ps.setString(2, createRequest.getTitle());
                ps.setInt(3, createRequest.getHeadcount());
                ps.setInt(4, 0);
            }

            @Override
            public int getBatchSize() {
                return createRequests.size();
            }
        });
    }
}
