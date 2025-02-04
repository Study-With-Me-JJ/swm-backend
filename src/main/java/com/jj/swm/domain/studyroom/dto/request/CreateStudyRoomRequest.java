package com.jj.swm.domain.studyroom.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jj.swm.domain.studyroom.entity.StudyRoomOption;
import com.jj.swm.domain.studyroom.entity.StudyRoomType;
import com.jj.swm.domain.studyroom.entity.embeddable.Address;
import com.jj.swm.domain.studyroom.entity.embeddable.Coordinates;
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

    @Size(max = 10)
    private List<String> tags;

    @Size(max = 7)
    private List<DayOfWeek> dayOffs;

    @Size(max = 20)
    @NotEmpty
    private List<String> imageUrls;

    @Size(max = 3)
    @NotEmpty
    private List<StudyRoomType> types;

    @NotEmpty
    private List<StudyRoomOption> options;

    @NotEmpty
    private List<CreateStudyRoomReservationTypeRequest> reservationTypes;

    @Min(1)
    @NotNull
    private Integer minReserveTime;

    @Min(1)
    @NotNull
    private Integer entireMinHeadcount;

    @Min(2)
    @NotNull
    private Integer entireMaxHeadcount;

    @NotNull
    @Positive
    private Integer entireMinPricePerHour;

    @NotNull
    @Positive
    private Integer entireMaxPricePerHour;
}
