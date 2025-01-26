package com.jj.swm.domain.external.study.dto.response;


import com.jj.swm.domain.external.study.entity.ExternalStudy;
import com.jj.swm.domain.external.study.enums.Role;
import com.jj.swm.domain.external.study.enums.Technology;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Builder
@Getter
public class ExternalStudyDto {
    private String id;

    @Schema(nullable = false)
    private String title;

    @Schema(nullable = false)
    private String link;

    @Schema(nullable = true)
    private List<Technology> technologies;

    @Schema(nullable = true)
    private List<Role> roles;

    @Schema(nullable = true)
    private LocalDate deadlineDate;

    public static ExternalStudyDto of(ExternalStudy externalStudy) {
        return ExternalStudyDto.builder()
                .id(externalStudy.getId())
                .title(externalStudy.getTitle())
                .link(externalStudy.getLink())
                .technologies(Arrays.stream(externalStudy.getTechnologies().split(",")).map(Technology::valueOf).toList())
                .roles(Arrays.stream(externalStudy.getRoles().split(",")).map(Role::valueOf).toList())
                .deadlineDate(externalStudy.getDeadlineDate())
                .build();
    }
}
