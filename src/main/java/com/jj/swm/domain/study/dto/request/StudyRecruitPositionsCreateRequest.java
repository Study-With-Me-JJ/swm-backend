package com.jj.swm.domain.study.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyRecruitPositionsCreateRequest {

    @NotBlank
    private String title;

    @Min(1)
    @NotNull
    private Integer headcount;
}
