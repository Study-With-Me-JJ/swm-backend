package com.jj.swm.domain.study.participation.dto.response;

import com.jj.swm.domain.study.core.entity.RecruitmentPositionTitle;
import com.jj.swm.domain.study.participation.entity.StudyParticipation;
import com.jj.swm.domain.study.participation.entity.StudyParticipationStatus;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class GetStudyParticipationInMyPageResponse {
    private Long participationId;

    private StudyParticipationStatus status;

    private String coverLetter;

    private UUID userId;

    private String profileImageUrl;

    private String nickname;

    private RecruitmentPositionTitle title;

    public static GetStudyParticipationInMyPageResponse from(StudyParticipation participation) {
        return GetStudyParticipationInMyPageResponse.builder()
                .participationId(participation.getId())
                .status(participation.getStatus())
                .coverLetter(participation.getCoverLetter())
                .userId(participation.getUser().getId())
                .profileImageUrl(participation.getUser().getProfileImageUrl())
                .nickname(participation.getUser().getNickname())
                .title(participation.getRecruitmentPosition().getTitle())
                .build();
    }
}
