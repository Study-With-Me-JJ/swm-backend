package com.jj.swm.domain.studyroom.dto.request;

import com.jj.swm.domain.studyroom.dto.request.update.option.StudyRoomOptionInfoModifyRequest;
import com.jj.swm.domain.studyroom.dto.request.update.reservationType.StudyRoomReservationTypeModifyRequest;
import com.jj.swm.domain.studyroom.dto.request.update.type.StudyRoomTypeInfoModifyRequest;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyRoomUpdateSettingRequest {

    @NotNull
    @Positive
    private Long studyRoomId;

    private StudyRoomOptionInfoModifyRequest optionInfoModification;

    private StudyRoomTypeInfoModifyRequest typeInfoModification;

    private StudyRoomReservationTypeModifyRequest reservationTypeModification;
}
