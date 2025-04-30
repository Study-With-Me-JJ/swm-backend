package com.jj.swm.domain.study.participation.dto.response;

import com.jj.swm.domain.study.participation.entity.StudyParticipation;
import com.jj.swm.domain.study.participation.entity.StudyParticipationLink;
import com.jj.swm.domain.study.participation.entity.StudyParticipationStatus;
import com.jj.swm.domain.study.participation.entity.embeddable.FileInfo;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

import static com.jj.swm.domain.study.participation.entity.StudyParticipationStatus.ACCEPTED;

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
            StudyParticipation participation,
            List<StudyParticipationLink> participationLinks,
            boolean isStudyWriter
    ) {
        return GetStudyParticipationDetailsResponse.builder()
                .participationId(participation.getId())
                .kakaoId(!isStudyWriter || (participation.getStatus() == ACCEPTED)
                        ? participation.getKakaoId() : null)
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
