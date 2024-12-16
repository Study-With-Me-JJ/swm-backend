package com.jj.swm.domain.studyroom.repository.jdbc;

import com.jj.swm.domain.studyroom.dto.request.CreateStudyRoomReservationTypeRequest;
import com.jj.swm.domain.studyroom.entity.StudyRoom;

import java.util.List;

public interface JdbcReserveTypeRepository {
    void batchInsert(List<CreateStudyRoomReservationTypeRequest> reservationTypes, StudyRoom studyRoom);
}
