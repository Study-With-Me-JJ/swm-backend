package com.jj.swm.domain.study.recruitmentposition.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ModifyRecruitmentPositionRequest extends AddRecruitmentPositionRequest {

    @Min(0)
    @Max(100)
    @NotNull
    private Integer acceptedCount;
}
