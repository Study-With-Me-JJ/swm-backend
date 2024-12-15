package com.jj.swm.domain.studyroom.repository.custom;

import com.jj.swm.domain.studyroom.dto.GetStudyRoomCondition;
import com.jj.swm.domain.studyroom.entity.StudyRoom;

import java.util.List;

public interface CustomStudyRoomRepository {

    List<StudyRoom> findAllWithPaginationAndCondition(int pageSize, GetStudyRoomCondition condition);
}
