package com.jj.swm.domain.study.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyRecruitPositionUpdateRequest {

    @NotNull
    @Positive
    private Long recruitPositionId;

    @NotNull
    private StudyRecruitPositionsCreateRequest recruitPositionChanges;
}