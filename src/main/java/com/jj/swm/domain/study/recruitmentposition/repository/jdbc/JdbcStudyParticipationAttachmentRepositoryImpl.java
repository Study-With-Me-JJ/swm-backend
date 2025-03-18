package com.jj.swm.domain.study.recruitmentposition.repository.jdbc;

import com.jj.swm.domain.study.recruitmentposition.entity.StudyParticipation;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class JdbcStudyParticipationAttachmentRepositoryImpl implements JdbcStudyParticipationAttachmentRepository {

    private final JdbcTemplate jdbcTemplate;

    public void batchInsert(List<String> fileUrls, StudyParticipation participation) {
        String sql = "insert into study_participation_attachment(study_participation_id, file_url) VALUES(?,?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                String fileUrl = fileUrls.get(i);

                ps.setLong(1, participation.getId());
                ps.setString(2, fileUrl);
            }

            @Override
            public int getBatchSize() {
                return fileUrls.size();
            }
        });
    }
}
