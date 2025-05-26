package com.jj.swm.domain.study.participation.dto.request;

import com.jj.swm.domain.study.participation.entity.StudyParticipation.StudyParticipationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateStudyParticipationStatusRequest {

    @NotNull
    private StudyParticipationStatus status;
}
