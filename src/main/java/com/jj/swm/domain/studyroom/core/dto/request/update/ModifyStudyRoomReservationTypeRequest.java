package com.jj.swm.domain.studyroom.core.dto.request.update;

import com.jj.swm.domain.studyroom.core.dto.request.CreateStudyRoomReservationTypeRequest;
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
