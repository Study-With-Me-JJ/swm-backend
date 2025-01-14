package com.jj.swm.domain.studyroom.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jj.swm.domain.studyroom.entity.StudyRoomReview;
import com.jj.swm.domain.studyroom.entity.StudyRoomReviewImage;
import com.jj.swm.domain.user.dto.response.UserInfoResponse;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class GetStudyRoomReviewResponse {

    private Long studyRoomReviewId;
    private String comment;
    private Integer rating;

    @JsonFormat(pattern = "yyyy.MM.dd HH:mm:ss")
    private LocalDateTime updatedAt;

    private List<String> imageUrls;
    private UserInfoResponse userInfo;
    private List<GetStudyRoomReviewReplyResponse> replies;

    public static GetStudyRoomReviewResponse from(
            StudyRoomReview studyRoomReview
    ) {
        return GetStudyRoomReviewResponse.builder()
                .studyRoomReviewId(studyRoomReview.getId())
                .comment(studyRoomReview.getComment())
                .rating(studyRoomReview.getRating())
                .updatedAt(studyRoomReview.getUpdatedAt())
                .imageUrls(studyRoomReview.getImages() != null
                        ? studyRoomReview.getImages()
                        .stream()
                        .map(StudyRoomReviewImage::getImageUrl)
                        .toList() : null
                )
                .userInfo(UserInfoResponse.from(studyRoomReview.getUser()))
                .replies(studyRoomReview.getReplies() != null
                        ? studyRoomReview.getReplies()
                        .stream()
                        .map(GetStudyRoomReviewReplyResponse::from)
                        .toList() : null
                )
                .build();
    }
}
