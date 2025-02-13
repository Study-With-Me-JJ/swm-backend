package com.jj.swm.domain.studyroom.core.dto.response;

import com.jj.swm.domain.studyroom.core.entity.StudyRoom;
import com.jj.swm.domain.studyroom.core.entity.StudyRoomBookmark;
import com.jj.swm.domain.studyroom.core.entity.embeddable.Coordinates;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GetStudyRoomResponse {

    private Long studyRoomId;
    private String title;
    private String thumbnail;
    private String locality;
    private String address;
    private String phoneNumber;
    private int likeCount;
    private int reviewCount;
    private int entireMinPricePerHour;
    private int entireMaxPricePerHour;
    private int entireMaxHeadcount;
    private double starAvg;
    private Coordinates coordinates;
    private Long studyBookmarkId;
    private List<GetStudyRoomTagResponse> tags;

    public static GetStudyRoomResponse of(StudyRoom studyRoom, Long studyBookmarkId) {
        return GetStudyRoomResponse.builder()
                .studyRoomId(studyRoom.getId())
                .title(studyRoom.getTitle())
                .thumbnail(studyRoom.getThumbnail())
                .locality(studyRoom.getAddress().getLocality())
                .address(studyRoom.getAddress().getAddress())
                .phoneNumber(studyRoom.getPhoneNumber())
                .likeCount(studyRoom.getLikeCount())
                .reviewCount(studyRoom.getReviewCount())
                .starAvg(studyRoom.getAverageRating())
                .entireMinPricePerHour(studyRoom.getEntireMinPricePerHour())
                .entireMaxPricePerHour(studyRoom.getEntireMaxPricePerHour())
                .entireMaxHeadcount(studyRoom.getEntireMaxHeadcount())
                .coordinates(studyRoom.getCoordinates())
                .studyBookmarkId(studyBookmarkId)
                .tags(studyRoom.getTags() != null ?
                        studyRoom.getTags().stream().map(GetStudyRoomTagResponse::from).toList()
                        : null
                )
                .build();
    }

    public static GetStudyRoomResponse of(StudyRoomBookmark studyRoomBookmark) {
        return GetStudyRoomResponse.of(studyRoomBookmark.getStudyRoom(), studyRoomBookmark.getId());
    }

}
