package com.jj.swm.domain.studyroom.dto;

import com.jj.swm.domain.studyroom.entity.StudyRoomOption;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class GetStudyRoomCondition {

    @Schema(description = "검색할 스터디 룸의 제목 (부분 일치 검색 가능)", example = "스터디 카페")
    private String title;

    @Schema(description = "검색할 지역 (예: '강남구')", example = "강남구")
    private String locality;

    @Schema(description = "최대 수용 인원", example = "10")
    @Min(0)
    @Max(50)
    private int headCount = 0;

    @Schema(description = "최소 시간당 가격 (기본값: 0)", example = "5000")
    @Min(0)
    @Max(300000)
    private int minPricePerHour = 0;

    @Schema(description = "최대 시간당 가격 (기본값: 300,000)", example = "20000")
    @Min(0)
    @Max(300000)
    private int maxPricePerHour = 300000;

    @Schema(description = "필터링할 옵션 목록", example = "[\"WIFI\", \"PARKING\"]")
    private List<StudyRoomOption> options = new ArrayList<>();

    @Schema(description = "정렬 기준 (예: STARS, LIKE, REVIEW, PRICE, DISTANCE)", example = "STARS")
    private SortCriteria sortCriteria = SortCriteria.STARS;

    @Schema(description = "사용자의 위도 (위치 기반 필터링에 사용)", example = "37.5665")
    private Double userLatitude;

    @Schema(description = "사용자의 경도 (위치 기반 필터링에 사용)", example = "126.9780")
    private Double userLongitude;

    @Schema(description = "페이징을 위한 마지막 조회된 스터디 룸 ID", example = "100")
    private Long lastStudyRoomId;

    @Schema(description = "페이징을 위한 마지막 정렬 값", example = "4")
    private Integer lastSortValue;

    @Schema(description = "페이징을 위한 마지막 위도 값", example = "37.1234")
    private Double lastLatitudeValue;

    @Schema(description = "페이징을 위한 마지막 경도 값", example = "127.5678")
    private Double lastLongitudeValue;

    @Schema(description = "페이징을 위한 마지막 평균 평점 값 (기본값: 5.0)", example = "4.5")
    private Double lastAverageRatingValue = 5.0;
}

