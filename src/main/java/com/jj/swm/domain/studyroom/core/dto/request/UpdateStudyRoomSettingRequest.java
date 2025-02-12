package com.jj.swm.domain.studyroom.core.dto.request;

import com.jj.swm.domain.studyroom.core.dto.request.update.ModifyStudyRoomOptionInfoRequest;
import com.jj.swm.domain.studyroom.core.dto.request.update.ModifyStudyRoomReservationTypeRequest;
import com.jj.swm.domain.studyroom.core.dto.request.update.ModifyStudyRoomTypeInfoRequest;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateStudyRoomSettingRequest {

    @NotNull
    @Positive
    private Integer entireMinPricePerHour;

    @NotNull
    @Positive
    private Integer entireMaxPricePerHour;

    private ModifyStudyRoomOptionInfoRequest optionInfoModification;

    private ModifyStudyRoomTypeInfoRequest typeInfoModification;

    private ModifyStudyRoomReservationTypeRequest reservationTypeModification;
}
