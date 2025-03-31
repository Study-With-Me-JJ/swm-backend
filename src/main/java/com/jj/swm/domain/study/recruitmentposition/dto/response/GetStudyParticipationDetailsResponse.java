package com.jj.swm.domain.study.recruitmentposition.dto.response;

import com.jj.swm.domain.study.recruitmentposition.entity.StudyParticipation;
import com.jj.swm.domain.study.recruitmentposition.entity.StudyParticipationLink;
import com.jj.swm.domain.study.recruitmentposition.entity.StudyParticipationStatus;
import com.jj.swm.domain.study.recruitmentposition.entity.embeddable.FileInfo;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class GetStudyParticipationDetailsResponse {

    private Long participationId;

    private String kakaoId;

    private StudyParticipationStatus status;

    private String coverLetter;

    private FileInfo fileInfo;

    private List<String> links;

    private UUID userId;

    private String profileImageUrl;

    private String nickname;

    public static GetStudyParticipationDetailsResponse of(
            StudyParticipation participation, List<StudyParticipationLink> participationLinks
    ) {
        return GetStudyParticipationDetailsResponse.builder()
                .participationId(participation.getId())
                .kakaoId(participation.getStatus() ==
                        StudyParticipationStatus.ACCEPTED ? participation.getKakaoId() : null)
                .status(participation.getStatus())
                .coverLetter(participation.getCoverLetter())
                .fileInfo(participation.getFileInfo())
                .links(participationLinks.stream()
                        .map(StudyParticipationLink::getLink)
                        .toList())
                .userId(participation.getUser().getId())
                .profileImageUrl(participation.getUser().getProfileImageUrl())
                .nickname(participation.getUser().getNickname())
                .build();
    }
}
