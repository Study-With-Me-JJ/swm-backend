package com.jj.swm.domain.study.core.dto.response;

import com.jj.swm.domain.study.core.dto.component.ParticipationStatusInfo;
import com.jj.swm.domain.study.core.dto.component.TagInfo;
import com.jj.swm.domain.study.core.dto.component.UserInteractionInfo;
import com.jj.swm.domain.study.core.entity.Study;
import com.jj.swm.domain.study.core.entity.Study.StudyCategory;
import com.jj.swm.domain.study.core.entity.Study.StudyStatus;
import com.jj.swm.domain.study.core.entity.StudyRecruitmentPosition;
import com.jj.swm.domain.study.core.entity.StudyRecruitmentPosition.RecruitmentPositionTitle;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GetStudyResponse {

    private Long studyId;

    private String title;

    private String content;

    private StudyCategory category;

    private int likeCount;

    private int commentCount;

    private StudyStatus status;

    private int viewCount;

    private UserInteractionInfo userInteractionInfo;

    private List<TagInfo> tagInfos;

    private List<RecruitmentPositionInfo> recruitmentPositionInfos;

    private ParticipationStatusInfo participationStatusInfo;

    public static GetStudyResponse of(
            Study study,
            UserInteractionInfo userInteractionInfo,
            ParticipationStatusInfo participationStatusInfo
    ) {
        return GetStudyResponse.builder()
                .studyId(study.getId())
                .title(study.getTitle())
                .content(study.getContent())
                .category(study.getCategory())
                .likeCount(study.getLikeCount())
                .commentCount(study.getCommentCount())
                .status(study.getStatus())
                .viewCount(study.getViewCount())
                .userInteractionInfo(userInteractionInfo)
                .tagInfos(study.getStudyTags().stream()
                        .map(TagInfo::from)
                        .toList())
                .recruitmentPositionInfos(study.getStudyRecruitmentPositions().stream()
                        .map(RecruitmentPositionInfo::from)
                        .toList())
                .participationStatusInfo(participationStatusInfo)
                .build();
    }

    public static GetStudyResponse from(Study study) {
        return GetStudyResponse.builder()
                .studyId(study.getId())
                .title(study.getTitle())
                .content(study.getContent())
                .category(study.getCategory())
                .likeCount(study.getLikeCount())
                .commentCount(study.getCommentCount())
                .status(study.getStatus())
                .viewCount(study.getViewCount())
                .tagInfos(study.getStudyTags().stream()
                        .map(TagInfo::from)
                        .toList())
                .recruitmentPositionInfos(study.getStudyRecruitmentPositions().stream()
                        .map(RecruitmentPositionInfo::from)
                        .toList())
                .build();
    }

    @Getter
    @Builder
    public static class RecruitmentPositionInfo {

        private Long recruitmentPositionId;

        private RecruitmentPositionTitle title;

        private Integer headcount;

        public static RecruitmentPositionInfo from(StudyRecruitmentPosition recruitmentPosition) {
            return RecruitmentPositionInfo.builder()
                    .recruitmentPositionId(recruitmentPosition.getId())
                    .title(recruitmentPosition.getTitle())
                    .headcount(recruitmentPosition.getHeadcount())
                    .build();
        }
    }
}
