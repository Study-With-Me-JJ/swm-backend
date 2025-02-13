package com.jj.swm.domain.studyroom.core.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jj.swm.domain.studyroom.core.constants.StudyRoomConstraints;
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

    @Size(max = StudyRoomConstraints.TAG_LIMIT)
    private List<String> tags;

    @Size(max = StudyRoomConstraints.DAYOFF_LIMIT)
    private List<DayOfWeek> dayOffs;

    @Size(max = StudyRoomConstraints.IMAGE_LIMIT)
    @NotEmpty
    private List<String> imageUrls;

    @Size(max = StudyRoomConstraints.TYPE_LIMIT)
    @NotEmpty
    private List<StudyRoomType> types;

    @NotEmpty
    private List<StudyRoomOption> options;

    @NotEmpty
    private List<CreateStudyRoomReservationTypeRequest> reservationTypes;

    @NotNull
    @Positive
    private Integer minReserveTime;

    @Max(StudyRoomConstraints.MAX_HEADCOUNT)
    @NotNull
    @Positive
    private Integer entireMinHeadcount;

    @Max(StudyRoomConstraints.MAX_HEADCOUNT)
    @NotNull
    @Positive
    private Integer entireMaxHeadcount;

    @Max(StudyRoomConstraints.MAX_PRICE_PER_HOUR)
    @NotNull
    @Positive
    private Integer entireMinPricePerHour;

    @Max(StudyRoomConstraints.MAX_PRICE_PER_HOUR)
    @NotNull
    @Positive
    private Integer entireMaxPricePerHour;
}
