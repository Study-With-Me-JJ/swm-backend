package com.jj.swm.domain.study.recruitmentposition.dto.response;

import com.jj.swm.domain.study.recruitmentposition.entity.StudyParticipation;
import com.jj.swm.domain.study.recruitmentposition.entity.StudyParticipationStatus;
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

    public static GetStudyParticipationResponse from(StudyParticipation studyParticipation) {
        return GetStudyParticipationResponse.builder()
                .participationId(studyParticipation.getId())
                .status(studyParticipation.getStatus())
                .coverLetter(studyParticipation.getCoverLetter())
                .userId(studyParticipation.getUser().getId())
                .profileImageUrl(studyParticipation.getUser().getProfileImageUrl())
                .nickname(studyParticipation.getUser().getNickname())
                .build();
    }
}
