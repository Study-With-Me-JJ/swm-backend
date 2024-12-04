package com.jj.swm.domain.studyroom.repository.jdbc;

import com.jj.swm.domain.studyroom.dto.request.StudyRoomReservationTypeCreateRequest;
import com.jj.swm.domain.studyroom.entity.StudyRoom;
import com.jj.swm.domain.studyroom.entity.StudyRoomType;

import java.util.List;

public interface JdbcReserveTypeRepository {
    void batchInsert(List<StudyRoomReservationTypeCreateRequest> reservationTypes, StudyRoom studyRoom);
}
