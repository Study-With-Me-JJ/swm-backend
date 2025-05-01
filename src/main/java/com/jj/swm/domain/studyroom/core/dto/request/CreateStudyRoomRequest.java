package com.jj.swm.domain.studyroom.core.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jj.swm.domain.studyroom.core.constants.StudyRoomConstants;
import com.jj.swm.domain.studyroom.core.entity.StudyRoomOption;
import com.jj.swm.domain.studyroom.core.entity.StudyRoomType;
import com.jj.swm.domain.studyroom.core.entity.embeddable.Address;
import com.jj.swm.domain.studyroom.core.entity.embeddable.Coordinates;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateStudyRoomRequest {

    @NotBlank
    private String title;

    private String subtitle;

    @NotBlank
    private String introduce;

    @NotBlank
    private String notice;

    @NotBlank
    private String guideline;

    @NotNull
    @Schema(example = "09:00")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime openingTime;

    @NotNull
    @Schema(example = "24:00")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime closingTime;

    @Valid
    @NotNull
    private Address address;

    @Valid
    @NotNull
    private Coordinates coordinates;

    @NotBlank
    private String thumbnail;

    private String referenceUrl;

    @NotBlank
    private String phoneNumber;

    @Size(max = StudyRoomConstants.TAG_LIMIT)
    private List<String> tags;

    @Size(max = StudyRoomConstants.DAYOFF_LIMIT)
    private List<DayOfWeek> dayOffs;

    @Size(max = StudyRoomConstants.IMAGE_LIMIT)
    @NotEmpty
    private List<String> imageUrls;

    @Size(max = StudyRoomConstants.TYPE_LIMIT)
    @NotEmpty
    private List<StudyRoomType> types;

    @NotEmpty
    private List<StudyRoomOption> options;

    @NotEmpty
    private List<CreateStudyRoomReserveTypeRequest> reserveTypes;

    @NotNull
    @Positive
    private Integer minReserveTime;

    @Max(StudyRoomConstants.MAX_HEADCOUNT)
    @NotNull
    @Positive
    private Integer entireMinHeadcount;

    @Max(StudyRoomConstants.MAX_HEADCOUNT)
    @NotNull
    @Positive
    private Integer entireMaxHeadcount;

    @Max(StudyRoomConstants.MAX_PRICE_PER_HOUR)
    @NotNull
    @Positive
    private Integer entireMinPricePerHour;

    @Max(StudyRoomConstants.MAX_PRICE_PER_HOUR)
    @NotNull
    @Positive
    private Integer entireMaxPricePerHour;
}
