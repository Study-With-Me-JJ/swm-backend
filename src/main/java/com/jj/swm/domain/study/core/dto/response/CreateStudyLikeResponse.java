package com.jj.swm.domain.study.core.dto.response;

import com.jj.swm.domain.study.core.entity.StudyLike;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateStudyLikeResponse {

    private Long likeId;

    public static CreateStudyLikeResponse from(StudyLike studyLike) {
        return CreateStudyLikeResponse.builder()
                .likeId(studyLike.getId())
                .build();
    }
}
