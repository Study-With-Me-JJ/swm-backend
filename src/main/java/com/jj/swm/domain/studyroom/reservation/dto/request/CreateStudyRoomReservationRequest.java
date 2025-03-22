package com.jj.swm.domain.studyroom.reservation.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateStudyRoomReservationRequest {

    @NotBlank
    private String reserverName;

    @NotBlank
    private String reserverPhoneNumber;

    @NotNull
    @Positive
    private Integer headcount;

    private String memo;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime checkInTime;

    @NotNull
    @Positive
    private Integer usageTime;

    @NotNull
    private Long studyRoomReserveTypeId;
}
