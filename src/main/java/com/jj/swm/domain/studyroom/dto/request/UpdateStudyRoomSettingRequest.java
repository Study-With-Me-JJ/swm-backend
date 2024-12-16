package com.jj.swm.domain.studyroom.dto.request;

import com.jj.swm.domain.studyroom.dto.request.update.option.ModifyStudyRoomOptionInfoRequest;
import com.jj.swm.domain.studyroom.dto.request.update.reservationType.ModifyStudyRoomReservationTypeRequest;
import com.jj.swm.domain.studyroom.dto.request.update.type.ModifyStudyRoomTypeInfoRequest;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateStudyRoomSettingRequest {

    private ModifyStudyRoomOptionInfoRequest optionInfoModification;

    private ModifyStudyRoomTypeInfoRequest typeInfoModification;

    private ModifyStudyRoomReservationTypeRequest reservationTypeModification;
}
