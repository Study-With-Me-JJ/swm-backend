package com.jj.swm.domain.studyroom.dto.response;

import com.jj.swm.domain.studyroom.entity.StudyRoom;
import com.jj.swm.domain.studyroom.entity.embeddable.Address;
import com.jj.swm.domain.studyroom.entity.embeddable.Point;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;
import java.util.List;

@Getter
@Builder
public class GetStudyRoomDetailResponse {

    private Long studyRoomId;
    private boolean likeStatus;
    private String title;
    private String subtitle;
    private String introduce;
    private String notice;
    private String guideline;
    private LocalTime openingTime;
    private LocalTime closingTime;
    private Address address;
    private Point point;
    private String referenceUrl;
    private String phoneNumber;
    private Integer minReserveTime;
    private Integer entireMinHeadcount;
    private Integer entireMaxHeadcount;
    private String thumbnail;
    private int likeCount;
    private int reviewCount;
    private double starAvg;
    private List<GetStudyRoomTagResponse> tags;
    private List<GetStudyRoomImageResponse> images;
    private List<GetStudyRoomReserveTypeResponse> reserveTypes;
    private List<GetStudyRoomDayOffResponse> dayOffs;
    private List<GetStudyRoomOptionInfoResponse> optionInfos;
    private List<GetStudyRoomTypeInfoResponse> typeInfos;

    public static GetStudyRoomDetailResponse of(
            StudyRoom studyRoom,
            boolean likeStatus,
            List<GetStudyRoomImageResponse> imageResponses,
            List<GetStudyRoomDayOffResponse> dayOffResponses,
            List<GetStudyRoomReserveTypeResponse> reserveTypeResponses,
            List<GetStudyRoomOptionInfoResponse> optionInfoResponses,
            List<GetStudyRoomTypeInfoResponse> typeInfoResponses
    ) {
        return GetStudyRoomDetailResponse.builder()
                .studyRoomId(studyRoom.getId())
                .likeStatus(likeStatus)
                .title(studyRoom.getTitle())
                .subtitle(studyRoom.getSubtitle())
                .introduce(studyRoom.getIntroduce())
                .notice(studyRoom.getNotice())
                .guideline(studyRoom.getGuideline())
                .openingTime(studyRoom.getOpeningTime())
                .closingTime(studyRoom.getClosingTime())
                .address(studyRoom.getAddress())
                .point(studyRoom.getPoint())
                .referenceUrl(studyRoom.getReferenceUrl())
                .phoneNumber(studyRoom.getPhoneNumber())
                .minReserveTime(studyRoom.getMinReserveTime())
                .entireMinHeadcount(studyRoom.getEntireMinHeadcount())
                .entireMaxHeadcount(studyRoom.getEntireMaxHeadcount())
                .thumbnail(studyRoom.getThumbnail())
                .likeCount(studyRoom.getLikeCount())
                .reviewCount(studyRoom.getReviewCount())
                .starAvg(studyRoom.getAverageRating())
                .tags(studyRoom.getTags().stream().map(GetStudyRoomTagResponse::from).toList())
                .images(imageResponses)
                .dayOffs(dayOffResponses)
                .optionInfos(optionInfoResponses)
                .typeInfos(typeInfoResponses)
                .reserveTypes(reserveTypeResponses)
                .build();
    }
}