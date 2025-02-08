package com.jj.swm.domain.study.repository.core;

import com.jj.swm.domain.study.dto.core.StudyBookmarkInfo;

import java.util.List;
import java.util.UUID;

public interface CustomStudyBookmarkRepository {

    List<StudyBookmarkInfo> findAllByUserIdAndStudyIds(UUID userId, List<Long> studyIds);
}
