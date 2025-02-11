package com.jj.swm.domain.study.core.dto.response;

import com.jj.swm.domain.study.core.entity.Study;
import com.jj.swm.domain.study.core.entity.StudyBookmark;
import com.jj.swm.domain.study.core.entity.StudyCategory;
import com.jj.swm.domain.study.core.entity.StudyStatus;
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

    public static StudyInquiryResponse of(StudyBookmark studyBookmark) {
        return StudyInquiryResponse.builder()
                .studyId(studyBookmark.getStudy().getId())
                .title(studyBookmark.getStudy().getTitle())
                .content(studyBookmark.getStudy().getContent())
                .category(studyBookmark.getStudy().getCategory())
                .likeCount(studyBookmark.getStudy().getLikeCount())
                .commentCount(studyBookmark.getStudy().getCommentCount())
                .status(studyBookmark.getStudy().getStatus())
                .viewCount(studyBookmark.getStudy().getViewCount())
                .studyBookmarkId(studyBookmark.getId())
                .tagInquiryListResponse(studyBookmark.getStudy().getStudyTags().stream()
                        .map(StudyTagInquiryResponse::from)
                        .toList())
                .build();
    }
}
