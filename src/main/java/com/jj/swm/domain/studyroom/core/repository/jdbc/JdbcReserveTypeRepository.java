package com.jj.swm.domain.studyroom.core.repository.jdbc;

import com.jj.swm.domain.studyroom.core.dto.request.CreateStudyRoomReserveTypeRequest;
import com.jj.swm.domain.studyroom.core.entity.StudyRoom;

import java.util.List;

public interface JdbcReserveTypeRepository {
    void batchInsert(List<CreateStudyRoomReserveTypeRequest> reservationTypes, StudyRoom studyRoom);
}
