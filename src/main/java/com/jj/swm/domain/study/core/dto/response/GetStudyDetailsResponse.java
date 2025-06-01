package com.jj.swm.domain.study.core.dto.response;

import com.jj.swm.domain.study.comment.dto.response.GetStudyParentCommentResponse;
import com.jj.swm.domain.study.core.dto.component.ParticipationStatusInfo;
import com.jj.swm.domain.study.core.dto.component.TagInfo;
import com.jj.swm.domain.study.core.dto.component.UserInteractionInfo;
import com.jj.swm.domain.study.core.entity.Study;
import com.jj.swm.domain.study.core.entity.Study.StudyCategory;
import com.jj.swm.domain.study.core.entity.Study.StudyStatistics;
import com.jj.swm.domain.study.core.entity.Study.StudyStatus;
import com.jj.swm.domain.study.core.entity.StudyImage;
import com.jj.swm.domain.study.core.entity.StudyRecruitmentPosition;
import com.jj.swm.domain.study.core.entity.StudyRecruitmentPosition.RecruitmentPositionTitle;
import com.jj.swm.domain.study.participation.repository.dto.StudyParticipationCountInfo;
import com.jj.swm.domain.user.core.dto.component.UserInfo;
import com.jj.swm.global.common.dto.PageResponse;
import com.jj.swm.global.common.dto.component.BaseTimeInfo;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GetStudyDetailsResponse {

    private Long studyId;

    private String title;

    private String content;

    private StudyCategory category;

    private StudyStatus status;

    private StudyStatistics statistics;

    private UserInfo userInfoResponse;

    private String openChatUrl;

    private UserInteractionInfo userInteractionInfo;

    private List<TagInfo> tagInfos;

    private List<ImageInfo> imageInfos;

    private List<RecruitmentPositionDetailsInfo> recruitmentPositionInfos;

    private PageResponse<GetStudyParentCommentResponse> pageComment;

    private ParticipationStatusInfo participationStatusInfo;

    private BaseTimeInfo baseTimeInfo;

    public static GetStudyDetailsResponse of(
            Study study,
            UserInteractionInfo userInteractionInfo,
            List<RecruitmentPositionDetailsInfo> recruitmentPositionInfos,
            List<ImageInfo> imageInfos,
            PageResponse<GetStudyParentCommentResponse> pageComment,
            ParticipationStatusInfo participationStatusInfo
    ) {
        return GetStudyDetailsResponse.builder()
                .studyId(study.getId())
                .title(study.getTitle())
                .content(study.getContent())
                .category(study.getCategory())
                .status(study.getStatus())
                .statistics(study.getStatistics())
                .userInfoResponse(UserInfo.from(study.getUser()))
                .openChatUrl(study.getOpenChatUrl())
                .userInteractionInfo(userInteractionInfo)
                .tagInfos(study.getStudyTags().stream()
                        .map(TagInfo::from)
                        .toList())
                .imageInfos(imageInfos)
                .recruitmentPositionInfos(recruitmentPositionInfos)
                .pageComment(pageComment)
                .baseTimeInfo(BaseTimeInfo.of(study.getCreatedAt(), study.getUpdatedAt()))
                .participationStatusInfo(participationStatusInfo)
                .build();
    }

    @Getter
    @Builder
    public static class RecruitmentPositionDetailsInfo {

        private Long recruitmentPositionId;

        private RecruitmentPositionTitle title;

        private Integer headcount;

        private RecruitmentPositionStat stat;

        public static RecruitmentPositionDetailsInfo of(
                StudyRecruitmentPosition recruitmentPosition,
                RecruitmentPositionStat stat
        ) {
            return RecruitmentPositionDetailsInfo.builder()
                    .recruitmentPositionId(recruitmentPosition.getId())
                    .title(recruitmentPosition.getTitle())
                    .headcount(recruitmentPosition.getHeadcount())
                    .stat(stat)
                    .build();
        }

        @Getter
        @Builder
        public static class RecruitmentPositionStat {

            private long participatedCount;

            private long acceptedCount;

            public static RecruitmentPositionStat from(StudyParticipationCountInfo participationCountInfo) {
                return RecruitmentPositionStat.builder()
                        .participatedCount(participationCountInfo.getParticipatedCount())
                        .acceptedCount(participationCountInfo.getAcceptedCount())
                        .build();
            }

            public static RecruitmentPositionStat empty() {
                return RecruitmentPositionStat.builder()
                        .participatedCount(0L)
                        .acceptedCount(0L)
                        .build();
            }
        }
    }

    @Getter
    @Builder
    public static class ImageInfo {

        private Long imageId;

        private String imageUrl;

        public static ImageInfo from(StudyImage image) {
            return ImageInfo.builder()
                    .imageId(image.getId())
                    .imageUrl(image.getImageUrl())
                    .build();
        }
    }
}
