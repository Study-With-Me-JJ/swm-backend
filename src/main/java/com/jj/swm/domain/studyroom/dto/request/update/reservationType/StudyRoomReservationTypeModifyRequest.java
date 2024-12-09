package com.jj.swm.domain.studyroom.dto.request.update.reservationType;

import com.jj.swm.domain.studyroom.dto.request.StudyRoomReservationTypeCreateRequest;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyRoomReservationTypeModifyRequest {

    private List<StudyRoomReservationTypeCreateRequest> reservationTypesToAdd;
    private List<StudyRoomReservationTypeUpdateRequest> reservationTypesToUpdate;
    private List<Long> reservationTypeIdsToRemove;
}
