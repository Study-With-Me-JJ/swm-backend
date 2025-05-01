package com.jj.swm.domain.study.participation.dto.response;

import com.jj.swm.domain.study.participation.entity.StudyParticipation;
import com.jj.swm.domain.study.participation.entity.StudyParticipationStatus;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class GetStudyParticipationResponse {

    private Long participationId;

    private StudyParticipationStatus status;

    private String coverLetter;

    private UUID userId;

    private String profileImageUrl;

    private String nickname;

    public static GetStudyParticipationResponse from(StudyParticipation participation) {
        return GetStudyParticipationResponse.builder()
                .participationId(participation.getId())
                .status(participation.getStatus())
                .coverLetter(participation.getCoverLetter())
                .userId(participation.getUser().getId())
                .profileImageUrl(participation.getUser().getProfileImageUrl())
                .nickname(participation.getUser().getNickname())
                .build();
    }
}
