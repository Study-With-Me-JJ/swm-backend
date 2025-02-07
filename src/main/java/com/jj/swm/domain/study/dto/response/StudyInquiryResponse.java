package com.jj.swm.domain.study.dto.response;

import com.jj.swm.domain.study.entity.Study;
import com.jj.swm.domain.study.entity.StudyCategory;
import com.jj.swm.domain.study.entity.StudyStatus;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class StudyInquiryResponse {

    private Long studyId;

    private String title;

    private String content;

    private StudyCategory category;

    private int likeCount;

    private int commentCount;

    private StudyStatus status;

    private int viewCount;

    private Long studyBookmarkId;

    private List<StudyTagInquiryResponse> tagInquiryListResponse;

    public static StudyInquiryResponse of(Study study, Long studyBookmarkId) {
        return StudyInquiryResponse.builder()
                .studyId(study.getId())
                .title(study.getTitle())
                .content(study.getContent())
                .category(study.getCategory())
                .likeCount(study.getLikeCount())
                .commentCount(study.getCommentCount())
                .status(study.getStatus())
                .viewCount(study.getViewCount())
                .studyBookmarkId(studyBookmarkId)
                .tagInquiryListResponse(study.getStudyTags().stream()
                        .map(StudyTagInquiryResponse::from)
                        .toList())
                .build();
    }
}
