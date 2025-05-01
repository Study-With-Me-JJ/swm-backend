package com.jj.swm.domain.studyroom.reservation.dto.request;

import com.jj.swm.domain.studyroom.reservation.entity.ApprovalStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateStudyRoomReservationApprovalStatusRequest {

    @NotNull
    private ApprovalStatus approvalStatus;
}
