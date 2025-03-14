package com.jj.swm.domain.study.core.repository.custom;

import com.jj.swm.domain.study.core.dto.StudyLikeInfo;

import java.util.List;
import java.util.UUID;

public interface CustomStudyLikeRepository {

    List<StudyLikeInfo> findAllByUserIdAndStudyIds(UUID userId, List<Long> studyIds);
}
