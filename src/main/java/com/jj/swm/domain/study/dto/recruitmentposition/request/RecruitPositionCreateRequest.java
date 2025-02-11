package com.jj.swm.domain.study.dto.recruitmentposition.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecruitPositionCreateRequest {

    @NotBlank
    private String title;

    @Min(1)
    @Max(100)
    @NotNull
    private Integer headcount;
}
