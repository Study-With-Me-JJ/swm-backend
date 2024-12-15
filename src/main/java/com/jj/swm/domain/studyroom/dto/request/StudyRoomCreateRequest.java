package com.jj.swm.domain.studyroom.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jj.swm.domain.studyroom.entity.StudyRoom;
import com.jj.swm.domain.studyroom.entity.StudyRoomOption;
import com.jj.swm.domain.studyroom.entity.StudyRoomType;
import com.jj.swm.domain.studyroom.entity.embeddable.Address;
import com.jj.swm.domain.studyroom.entity.embeddable.Point;
import com.jj.swm.domain.user.entity.User;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyRoomCreateRequest {

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
    @JsonFormat(pattern = "HH:mm")
    private LocalTime openingTime;

    @NotNull
    @JsonFormat(pattern = "HH:mm")
    private LocalTime closingTime;

    @NotNull
    private Address address;

    private Point point;

    @NotBlank
    private String thumbnail;

    private String referenceUrl;

    @NotBlank
    private String phoneNumber;

    private List<String> tags;

    private List<DayOfWeek> dayOffs;

    @NotEmpty
    private List<String> imageUrls;

    @NotEmpty
    private List<StudyRoomType> types;

    @NotEmpty
    private List<StudyRoomOption> options;

    @NotEmpty
    private List<StudyRoomReservationTypeCreateRequest> reservationTypes;

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
