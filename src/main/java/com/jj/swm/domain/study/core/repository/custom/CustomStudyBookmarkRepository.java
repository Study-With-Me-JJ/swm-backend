package com.jj.swm.domain.study.core.repository.custom;

import com.jj.swm.domain.study.core.dto.StudyBookmarkInfo;

import java.util.List;
import java.util.UUID;

public interface CustomStudyBookmarkRepository {

    List<StudyBookmarkInfo> findAllByUserIdAndStudyIdList(UUID userId, List<Long> studyIdList);
}
