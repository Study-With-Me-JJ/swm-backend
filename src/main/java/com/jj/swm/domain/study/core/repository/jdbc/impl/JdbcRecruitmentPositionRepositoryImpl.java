package com.jj.swm.domain.study.core.repository.jdbc.impl;

import com.jj.swm.domain.study.core.dto.component.CreateRecruitmentPositionInfo;
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

    public void batchInsert(List<CreateRecruitmentPositionInfo> infos, Study study) {
        String sql = "insert into study_recruitment_position(title, headcount, study_id) VALUES(?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                CreateRecruitmentPositionInfo info = infos.get(i);

                ps.setObject(1, info.getTitle().name(), Types.OTHER);
                ps.setInt(2, info.getHeadcount());
                ps.setLong(3, study.getId());
            }

            @Override
            public int getBatchSize() {
                return infos.size();
            }
        });
    }
}
