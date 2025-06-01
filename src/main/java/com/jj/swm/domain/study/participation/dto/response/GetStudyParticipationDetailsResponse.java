package com.jj.swm.domain.study.participation.dto.response;

import com.jj.swm.domain.study.participation.entity.StudyParticipation;
import com.jj.swm.domain.study.participation.entity.StudyParticipation.StudyParticipationStatus;
import com.jj.swm.domain.study.participation.entity.StudyParticipationLink;
import com.jj.swm.domain.study.participation.entity.embeddable.FileInfo;
import com.jj.swm.domain.user.core.dto.component.UserInfo;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

import static com.jj.swm.domain.study.participation.entity.StudyParticipation.StudyParticipationStatus.ACCEPTED;

@Getter
@Builder
public class GetStudyParticipationDetailsResponse {

    private Long participationId;

    private String kakaoId;

    private StudyParticipationStatus status;

    private String coverLetter;

    private FileInfo fileInfo;

    private List<LinkInfo> linkInfos;

    private UserInfo userInfo;

    public static GetStudyParticipationDetailsResponse of(
            StudyParticipation participation,
            List<LinkInfo> linkInfos,
            boolean isStudyWriter
    ) {
        return GetStudyParticipationDetailsResponse.builder()
                .participationId(participation.getId())
                .kakaoId((!isStudyWriter || (participation.getStatus() == ACCEPTED)) ? participation.getKakaoId() : null)
                .status(participation.getStatus())
                .coverLetter(participation.getCoverLetter())
                .fileInfo(participation.getFileInfo())
                .linkInfos(linkInfos)
                .userInfo(UserInfo.from(participation.getUser()))
                .build();
    }

    @Getter
    @Builder
    public static class LinkInfo {

        private Long linkId;

        private String link;

        public static LinkInfo from(StudyParticipationLink link) {
            return LinkInfo.builder()
                    .linkId(link.getId())
                    .link(link.getLink())
                    .build();
        }
    }
}
