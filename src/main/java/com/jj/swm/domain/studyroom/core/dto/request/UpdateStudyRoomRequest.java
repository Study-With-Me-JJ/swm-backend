package com.jj.swm.domain.studyroom.core.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jj.swm.domain.studyroom.core.dto.request.update.ModifyStudyRoomDayOffRequest;
import com.jj.swm.domain.studyroom.core.dto.request.update.ModifyStudyRoomImageRequest;
import com.jj.swm.domain.studyroom.core.dto.request.update.ModifyStudyRoomTagRequest;
import com.jj.swm.domain.studyroom.core.entity.embeddable.Address;
import com.jj.swm.domain.studyroom.core.entity.embeddable.Coordinates;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalTime;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateStudyRoomRequest {

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

    private Coordinates coordinates;

    @NotBlank
    private String thumbnail;

    private String referenceUrl;

    @NotBlank
    private String phoneNumber;

    @Min(1)
    @NotNull
    private Integer minReserveTime;

    @Min(1)
    @NotNull
    private Integer entireMinHeadcount;

    @Min(2)
    @NotNull
    private Integer entireMaxHeadcount;

    private ModifyStudyRoomImageRequest imageModification;

    private ModifyStudyRoomTagRequest tagModification;

    private ModifyStudyRoomDayOffRequest dayOffModification;

}
