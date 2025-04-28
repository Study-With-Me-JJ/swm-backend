package com.jj.swm.domain.study.core.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jj.swm.domain.study.comment.dto.response.GetStudyCommentResponse;
import com.jj.swm.domain.study.core.dto.UserInteractionInfo;
import com.jj.swm.domain.study.core.entity.Study;
import com.jj.swm.domain.study.core.entity.StudyCategory;
import com.jj.swm.domain.study.core.entity.StudyStatus;
import com.jj.swm.domain.user.core.dto.response.UserInfoResponse;
import com.jj.swm.global.common.dto.PageResponse;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class GetStudyDetailsResponse {

    private Long studyId;

    private String title;

    private String content;

    private StudyCategory category;

    private int likeCount;

    private int commentCount;

    private StudyStatus status;

    private int viewCount;

    private UserInfoResponse userInfoResponse;

    private String openChatUrl;

    private UserInteractionInfo userInteractionInfo;

    private List<GetStudyTagResponse> getTagResponses;

    private List<GetStudyImageResponse> getImageResponses;

    private List<GetRecruitmentPositionDetailsResponse> getRecruitmentPositionResponses;

    private PageResponse<GetStudyCommentResponse> pageCommentResponse;

    private GetStudyParticipationStatusResponse getStudyParticipationStatusResponse;

    @JsonFormat(pattern = "yy.MM.dd HH:mm")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yy.MM.dd HH:mm")
    private LocalDateTime updatedAt;

    public static GetStudyDetailsResponse of(
            Study study,
            UserInteractionInfo userInteractionInfo,
            List<GetRecruitmentPositionDetailsResponse> getRecruitmentPositionDetailsResponses,
            List<GetStudyImageResponse> getImageResponses,
            PageResponse<GetStudyCommentResponse> pageCommentResponse,
            GetStudyParticipationStatusResponse getStudyParticipationStatusResponse
    ) {
        return GetStudyDetailsResponse.builder()
                .studyId(study.getId())
                .title(study.getTitle())
                .content(study.getContent())
                .category(study.getCategory())
                .likeCount(study.getLikeCount())
                .commentCount(study.getCommentCount())
                .status(study.getStatus())
                .viewCount(study.getViewCount())
                .userInfoResponse(UserInfoResponse.from(study.getUser()))
                .openChatUrl(study.getOpenChatUrl())
                .userInteractionInfo(userInteractionInfo)
                .getTagResponses(study.getStudyTags().stream()
                        .map(GetStudyTagResponse::from)
                        .toList())
                .getImageResponses(getImageResponses)
                .getRecruitmentPositionResponses(getRecruitmentPositionDetailsResponses)
                .pageCommentResponse(pageCommentResponse)
                .createdAt(study.getCreatedAt())
                .updatedAt(study.getUpdatedAt())
                .getStudyParticipationStatusResponse(getStudyParticipationStatusResponse)
                .build();
    }
}
