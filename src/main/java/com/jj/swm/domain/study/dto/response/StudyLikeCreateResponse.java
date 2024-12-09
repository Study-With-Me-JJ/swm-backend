package com.jj.swm.domain.study.dto.response;

import com.jj.swm.domain.study.entity.StudyLike;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StudyLikeCreateResponse {

    private Long likeId;

    public static StudyLikeCreateResponse from(StudyLike studyLike) {
        return StudyLikeCreateResponse.builder()
                .likeId(studyLike.getId())
                .build();
    }
}
