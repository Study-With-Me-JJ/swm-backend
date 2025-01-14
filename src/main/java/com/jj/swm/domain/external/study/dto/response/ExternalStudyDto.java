package com.jj.swm.domain.external.study.dto.response;


import com.jj.swm.domain.external.study.entity.ExternalStudy;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Builder
@Getter
public class ExternalStudyDto {
    private String id;

    @Schema(nullable = false)
    private String title;

    @Schema(nullable = false)
    private String link;

    @Schema(nullable = true)
    private String technologies;

    @Schema(nullable = true)
    private String roles;

    @Schema(nullable = true)
    private LocalDate deadlineDate;

    public static ExternalStudyDto of(ExternalStudy externalStudy) {
        return ExternalStudyDto.builder()
                .id(externalStudy.getId())
                .title(externalStudy.getTitle())
                .link(externalStudy.getLink())
                .technologies(externalStudy.getTechnologies())
                .roles(externalStudy.getRoles())
                .deadlineDate(externalStudy.getDeadlineDate())
                .build();
    }
}
