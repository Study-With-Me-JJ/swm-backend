package com.jj.swm.domain.studyroom.dto.request.update.reservationType;

import com.jj.swm.domain.studyroom.dto.request.StudyRoomReservationTypeCreateRequest;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyRoomReservationTypeUpdateRequest {

    @NotNull
    @Positive
    private Long reservationTypeId;

    @NotNull
    private StudyRoomReservationTypeCreateRequest reservationType;
}
