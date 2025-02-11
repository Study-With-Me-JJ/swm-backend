package com.jj.swm.domain.studyroom.core.repository.custom;

import com.jj.swm.domain.studyroom.core.dto.GetStudyRoomCondition;
import com.jj.swm.domain.studyroom.core.entity.StudyRoom;

import java.util.List;

public interface CustomStudyRoomRepository {

    List<StudyRoom> findAllWithPaginationAndCondition(int pageSize, GetStudyRoomCondition condition);
}
