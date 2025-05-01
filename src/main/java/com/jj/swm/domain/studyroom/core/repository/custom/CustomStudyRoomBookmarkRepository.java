package com.jj.swm.domain.studyroom.core.repository.custom;

import com.jj.swm.domain.studyroom.core.dto.StudyRoomBookmarkInfo;

import java.util.List;
import java.util.UUID;

public interface CustomStudyRoomBookmarkRepository {

    List<StudyRoomBookmarkInfo> findAllByUserIdAndStudyRoomIds(UUID userId, List<Long> studyRoomIds);
}
