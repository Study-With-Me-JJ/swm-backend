package com.jj.swm.domain.studyroom.core.dto.request.update;

import com.jj.swm.domain.studyroom.core.dto.request.CreateStudyRoomReservationTypeRequest;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateStudyRoomReservationTypeRequest {

    @NotNull
    @Positive
    private Long reservationTypeId;

    @NotNull
    private CreateStudyRoomReservationTypeRequest reservationType;
}
