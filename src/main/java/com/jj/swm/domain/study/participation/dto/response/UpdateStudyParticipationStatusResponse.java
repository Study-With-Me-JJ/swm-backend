package com.jj.swm.domain.study.participation.dto.response;

import com.jj.swm.domain.study.participation.entity.StudyParticipation;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateStudyParticipationStatusResponse {

    private String kakaoId;

    public static UpdateStudyParticipationStatusResponse from(StudyParticipation participation) {
        return UpdateStudyParticipationStatusResponse.builder()
                .kakaoId(participation.getKakaoId())
                .build();
    }
}
