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

    @Schema(description = "이 값을 기준으로 다음 스터디 목록 조회 (댓글 많은 순, 좋아요 많은 순일 때도 필요)")
    private Long lastStudyId;

    private SortCriteria sortCriteria = NEWEST;

    @Schema(description = "정렬 기준이 댓글 많은 순, 좋아요 많은 순일 때 이 값을 기준으로 다음 스터디 목록 조회")
    private Integer lastSortValue;

    @Schema(defaultValue = "NEWEST", description = "최신순, 댓글 많은 순, 좋아요 많은 순 정렬 지원")
    public enum SortCriteria {
        LIKE, NEWEST, COMMENT
    }
}
