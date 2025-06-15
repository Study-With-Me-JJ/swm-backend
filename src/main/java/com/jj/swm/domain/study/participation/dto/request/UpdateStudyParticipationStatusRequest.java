package com.jj.swm.domain.study.participation.dto.request;

import com.jj.swm.domain.study.participation.entity.StudyParticipation.StudyParticipationStatus;
import com.jj.swm.global.common.annotation.validation.EnumMatch;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateStudyParticipationStatusRequest {

    @NotNull
    @EnumMatch(excludes = "PENDING")
    private StudyParticipationStatus status;
}
