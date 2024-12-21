package com.jj.swm.domain.study.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecruitPositionUpsertRequest {

    @NotBlank
    private String title;

    @Min(1)
    @Max(100)
    @NotNull
    private Integer headcount;

    @Min(0)
    @Max(100)
    @NotNull
    private Integer acceptedCount;
}
