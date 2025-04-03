package com.jj.swm.domain.study.recruitmentposition.dto.response;

import com.jj.swm.domain.study.recruitmentposition.entity.StudyParticipation;
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
