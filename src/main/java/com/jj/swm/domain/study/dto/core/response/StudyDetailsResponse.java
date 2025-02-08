package com.jj.swm.domain.study.dto.core.response;

import com.jj.swm.domain.study.dto.comment.response.CommentInquiryResponse;
import com.jj.swm.domain.study.dto.recruitmentposition.response.RecruitPositionInquiryResponse;
import com.jj.swm.domain.study.entity.core.Study;
import com.jj.swm.global.common.dto.PageResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class StudyDetailsResponse {

    private UUID userId;

    private String nickname;

    private String profileImageUrl;

    private boolean likeStatus;

    private int viewCount;

    private String openChatUrl;

    private List<StudyImageInquiryResponse> imageInquiryResponses;

    private List<RecruitPositionInquiryResponse> recruitPositionInquiryResponses;

    private PageResponse<CommentInquiryResponse> commentPageResponse;

    public static StudyDetailsResponse of(
            boolean likeStatus,
            Study study,
            List<StudyImageInquiryResponse> imageInquiryResponses,
            List<RecruitPositionInquiryResponse> recruitPositionInquiryResponses,
            PageResponse<CommentInquiryResponse> commentPageResponse
    ) {
        return StudyDetailsResponse.builder()
                .userId(study.getUser().getId())
                .nickname(study.getUser().getNickname())
                .profileImageUrl(study.getUser().getProfileImageUrl())
                .likeStatus(likeStatus)
                .viewCount(study.getViewCount())
                .openChatUrl(study.getOpenChatUrl())
                .imageInquiryResponses(imageInquiryResponses)
                .recruitPositionInquiryResponses(recruitPositionInquiryResponses)
                .commentPageResponse(commentPageResponse)
                .build();
    }
}
