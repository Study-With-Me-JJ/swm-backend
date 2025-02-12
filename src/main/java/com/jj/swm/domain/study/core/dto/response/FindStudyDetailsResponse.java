package com.jj.swm.domain.study.core.dto.response;

import com.jj.swm.domain.study.comment.dto.response.FindParentCommentResponse;
import com.jj.swm.domain.study.recruitmentposition.dto.response.FindRecruitmentPositionResponse;
import com.jj.swm.domain.study.core.entity.Study;
import com.jj.swm.global.common.dto.PageResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class FindStudyDetailsResponse {

    private UUID userId;

    private String nickname;

    private String profileImageUrl;

    private boolean likeStatus;

    private int viewCount;

    private String openChatUrl;

    private List<FindStudyImageResponse> findImageResponseList;

    private List<FindRecruitmentPositionResponse> findRecruitmetPositionResponseList;

    private PageResponse<FindParentCommentResponse> pageCommentResponse;

    public static FindStudyDetailsResponse of(
            boolean likeStatus,
            Study study,
            List<FindStudyImageResponse> findImageResponseList,
            List<FindRecruitmentPositionResponse> findRecruitmetPositionResponseList,
            PageResponse<FindParentCommentResponse> pageCommentResponse
    ) {
        return FindStudyDetailsResponse.builder()
                .userId(study.getUser().getId())
                .nickname(study.getUser().getNickname())
                .profileImageUrl(study.getUser().getProfileImageUrl())
                .likeStatus(likeStatus)
                .viewCount(study.getViewCount())
                .openChatUrl(study.getOpenChatUrl())
                .findImageResponseList(findImageResponseList)
                .findRecruitmetPositionResponseList(findRecruitmetPositionResponseList)
                .pageCommentResponse(pageCommentResponse)
                .build();
    }
}
