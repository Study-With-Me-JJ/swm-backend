package com.jj.swm.domain.studyroom.repository.jdbc.impl;

import com.jj.swm.domain.studyroom.dto.request.StudyRoomReservationTypeCreateRequest;
import com.jj.swm.domain.studyroom.entity.StudyRoom;
import com.jj.swm.domain.studyroom.repository.jdbc.JdbcReserveTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@RequiredArgsConstructor
public class JdbcReserveTypeRepositoryImpl implements JdbcReserveTypeRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void batchInsert(List<StudyRoomReservationTypeCreateRequest> reservationTypes, StudyRoom studyRoom) {
        String sql = "insert into study_room_reserve_type (study_room_id, max_headcount, reservation_option, price_per_hour, created_at, updated_at) values (?, ?, ?, ?, NOW(), NOW())";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, studyRoom.getId());
                ps.setInt(2, reservationTypes.get(i).getMaxHeadcount());
                ps.setString(3, reservationTypes.get(i).getReservationOption());
                ps.setInt(4, reservationTypes.get(i).getPricePerHour());
            }

            @Override
            public int getBatchSize() {
                return reservationTypes.size();
            }
        });
    }
}
