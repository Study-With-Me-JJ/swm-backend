package com.jj.swm.domain.study.dto.response;

import com.jj.swm.global.common.dto.PageResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class StudyDetailsResponse {

    private boolean likeStatus;

    private int viewCount;

    private String openChatUrl;

    private List<StudyImageInquiryResponse> imageInquiryResponses;

    private List<RecruitPositionInquiryResponse> recruitPositionInquiryResponses;

    private PageResponse<CommentInquiryResponse> commentPageResponse;

    public static StudyDetailsResponse of(
            boolean likeStatus,
            int viewCount,
            String openChatUrl,
            List<StudyImageInquiryResponse> imageInquiryResponses,
            List<RecruitPositionInquiryResponse> recruitPositionInquiryResponses,
            PageResponse<CommentInquiryResponse> commentPageResponse) {
        return StudyDetailsResponse.builder()
                .likeStatus(likeStatus)
                .viewCount(viewCount)
                .openChatUrl(openChatUrl)
                .imageInquiryResponses(imageInquiryResponses)
                .recruitPositionInquiryResponses(recruitPositionInquiryResponses)
                .commentPageResponse(commentPageResponse)
                .build();
    }
}
