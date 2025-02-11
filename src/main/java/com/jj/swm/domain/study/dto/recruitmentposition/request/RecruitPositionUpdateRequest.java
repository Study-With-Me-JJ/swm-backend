package com.jj.swm.domain.study.dto.recruitmentposition.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecruitPositionUpdateRequest extends RecruitPositionCreateRequest{

    @Min(0)
    @Max(100)
    @NotNull
    private Integer acceptedCount;
}
