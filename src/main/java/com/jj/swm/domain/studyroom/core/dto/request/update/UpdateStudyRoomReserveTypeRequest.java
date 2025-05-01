package com.jj.swm.domain.studyroom.core.dto.request.update;

import com.jj.swm.domain.studyroom.core.dto.request.CreateStudyRoomReserveTypeRequest;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateStudyRoomReserveTypeRequest {

    @NotNull
    @Positive
    private Long reserveTypeId;

    @NotNull
    private CreateStudyRoomReserveTypeRequest reserveType;
}
