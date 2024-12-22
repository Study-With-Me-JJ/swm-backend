package com.jj.swm.domain.studyroom.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jj.swm.domain.studyroom.entity.StudyRoomReview;
import com.jj.swm.domain.studyroom.entity.StudyRoomReviewImage;
import com.jj.swm.domain.studyroom.entity.StudyRoomReviewReply;
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

    public static GetStudyRoomReviewResponse of(
            StudyRoomReview studyRoomReview, List<StudyRoomReviewReply> replies
    ) {
        return GetStudyRoomReviewResponse.builder()
                .studyRoomReviewId(studyRoomReview.getId())
                .comment(studyRoomReview.getComment())
                .rating(studyRoomReview.getRating())
                .updatedAt(studyRoomReview.getUpdatedAt())
                .imageUrls(studyRoomReview.getImages()
                        .stream()
                        .map(StudyRoomReviewImage::getImageUrl)
                        .toList())
                .userInfo(UserInfoResponse.from(studyRoomReview.getUser()))
                .replies(replies.stream()
                        .map(GetStudyRoomReviewReplyResponse::from)
                        .toList()
                )
                .build();
    }
}