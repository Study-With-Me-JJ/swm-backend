package com.jj.swm.domain.studyroom.dto;

import com.jj.swm.domain.studyroom.entity.StudyRoomOption;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class GetStudyRoomCondition {

    private String title;

    private String locality;

    @Max(50)
    @Positive
    private int headCount;

    @Max(300000)
    @Positive
    private int minPricePerHour = 0;

    @Max(300000)
    @Positive
    private int maxPricePerHour = 300000;

    private List<StudyRoomOption> options = new ArrayList<>();

    private SortCriteria sortCriteria = SortCriteria.STARS;

    private Double userLatitude;

    private Double userLongitude;

    private Long lastStudyRoomId;

    private Integer lastSortValue;

    private Double lastLatitudeValue;

    private Double lastLongitudeValue;

    private Double lastAverageRatingValue = 5.0;
}

