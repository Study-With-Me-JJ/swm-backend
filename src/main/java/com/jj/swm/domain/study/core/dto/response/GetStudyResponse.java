package com.jj.swm.domain.study.core.dto.response;

import com.jj.swm.domain.study.core.entity.Study;
import com.jj.swm.domain.study.core.entity.StudyCategory;
import com.jj.swm.domain.study.core.entity.StudyStatus;
import com.jj.swm.domain.study.recruitmentposition.dto.response.GetRecruitmentPositionResponse;
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

    private Long studyBookmarkId;

    private boolean liked;

    private List<GetStudyTagResponse> getTagResponseList;

    private List<GetRecruitmentPositionResponse> getRecruitmentPositionResponseList;

    public static GetStudyResponse of(
            Study study,
            Long studyBookmarkId,
            boolean liked
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
                .studyBookmarkId(studyBookmarkId)
                .liked(liked)
                .getTagResponseList(study.getStudyTagList().stream()
                        .map(GetStudyTagResponse::from)
                        .toList())
                .getRecruitmentPositionResponseList(study.getStudyRecruitmentPositionList().stream()
                        .map(GetRecruitmentPositionResponse::from)
                        .toList())
                .build();
    }

    public static GetStudyResponse of(Study study) {
        return GetStudyResponse.builder()
                .studyId(study.getId())
                .title(study.getTitle())
                .content(study.getContent())
                .category(study.getCategory())
                .likeCount(study.getLikeCount())
                .commentCount(study.getCommentCount())
                .status(study.getStatus())
                .viewCount(study.getViewCount())
                .studyBookmarkId(null)
                .liked(false)
                .getTagResponseList(study.getStudyTagList().stream()
                        .map(GetStudyTagResponse::from)
                        .toList())
                .getRecruitmentPositionResponseList(study.getStudyRecruitmentPositionList().stream()
                        .map(GetRecruitmentPositionResponse::from)
                        .toList())
                .build();
    }
}
