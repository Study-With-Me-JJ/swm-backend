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

    private List<StudyImageInquiryResponse> imageInquiryResponses;

    private List<StudyRecruitPositionInquiryResponse> recruitPositionInquiryResponses;

    private PageResponse<CommentInquiryResponse> commentPageResponse;

    public static StudyDetailsResponse of(
            boolean likeStatus,
            int viewCount,
            List<StudyImageInquiryResponse> imageInquiryResponses,
            List<StudyRecruitPositionInquiryResponse> recruitPositionInquiryResponses,
            PageResponse<CommentInquiryResponse> commentPageResponse) {
        return StudyDetailsResponse.builder()
                .likeStatus(likeStatus)
                .viewCount(viewCount)
                .imageInquiryResponses(imageInquiryResponses)
                .recruitPositionInquiryResponses(recruitPositionInquiryResponses)
                .commentPageResponse(commentPageResponse)
                .build();
    }
}
