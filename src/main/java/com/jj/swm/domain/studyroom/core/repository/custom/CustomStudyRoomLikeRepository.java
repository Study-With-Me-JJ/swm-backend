package com.jj.swm.domain.studyroom.core.repository.custom;

import com.jj.swm.domain.studyroom.core.dto.StudyRoomLikeInfo;

import java.util.List;
import java.util.UUID;

public interface CustomStudyRoomLikeRepository {
    List<StudyRoomLikeInfo> findAllByUserIdAndStudyRoomIds(UUID userId, List<Long> studyRoomIds);
}
