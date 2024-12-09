package com.jj.swm.domain.study.dto.request;

import com.jj.swm.domain.study.entity.StudyCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyUpdateRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @NotNull
    private StudyCategory category;

    private String thumbnail;

    private StudyTagModifyRequest tagModifyRequest;

    private StudyImageModifyRequest imageModifyRequest;

    private StudyRecruitPositionModifyRequest recruitPositionModifyRequest;
}
