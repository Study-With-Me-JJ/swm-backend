package com.jj.swm.domain.study.core.dto;

import com.jj.swm.domain.study.core.entity.RecruitmentPositionTitle;
import com.jj.swm.domain.study.core.entity.StudyCategory;
import com.jj.swm.domain.study.core.entity.StudyStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GetStudyCondition {

    private String title;

    private StudyCategory category;

    private StudyStatus status;

    private List<RecruitmentPositionTitle> recruitmentPositionTitles;

    private Long lastStudyId;

    @Schema(defaultValue = "NEWEST")
    private SortCriteria sortCriteria = SortCriteria.NEWEST;

    private Integer lastSortValue;
}
