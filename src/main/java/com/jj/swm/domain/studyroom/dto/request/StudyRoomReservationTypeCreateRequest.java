package com.jj.swm.domain.studyroom.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class StudyRoomReservationTypeCreateRequest {

    @Min(1)
    @NotNull
    private Integer maxHeadcount;

    @NotBlank
    private String reservationOption;

    @Positive
    @NotNull
    private Integer pricePerHour;

}
