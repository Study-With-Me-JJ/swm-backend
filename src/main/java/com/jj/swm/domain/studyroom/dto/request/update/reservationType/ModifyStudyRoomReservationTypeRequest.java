package com.jj.swm.domain.studyroom.dto.request.update.reservationType;

import com.jj.swm.domain.studyroom.dto.request.CreateStudyRoomReservationTypeRequest;
import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ModifyStudyRoomReservationTypeRequest {

    private List<CreateStudyRoomReservationTypeRequest> reservationTypesToAdd;
    private List<UpdateStudyRoomReservationTypeRequest> reservationTypesToUpdate;
    private List<Long> reservationTypeIdsToRemove;
}
