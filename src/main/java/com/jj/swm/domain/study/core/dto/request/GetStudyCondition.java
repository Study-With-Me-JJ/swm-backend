package com.jj.swm.domain.study.core.dto.request;

import com.jj.swm.domain.study.core.entity.Study.StudyCategory;
import com.jj.swm.domain.study.core.entity.Study.StudyStatus;
import com.jj.swm.domain.study.core.entity.StudyRecruitmentPosition.RecruitmentPositionTitle;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

import static com.jj.swm.domain.study.core.dto.request.GetStudyCondition.SortCriteria.NEWEST;

@Getter
@Setter
public class GetStudyCondition {

    private String title;

    private StudyCategory category;

    private StudyStatus status;

    private List<RecruitmentPositionTitle> recruitmentPositionTitles;

    private Long lastStudyId;

    @Schema(defaultValue = "NEWEST")
    private SortCriteria sortCriteria = NEWEST;

    private Integer lastSortValue;

    public enum SortCriteria {
        LIKE, NEWEST, COMMENT
    }
}
