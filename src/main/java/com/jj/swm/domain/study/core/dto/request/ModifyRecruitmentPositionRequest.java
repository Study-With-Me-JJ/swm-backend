package com.jj.swm.domain.study.core.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

import static com.jj.swm.domain.study.core.constants.StudyConstants.RECRUITMENT_POSITION_LIMIT;

@Getter
@SuperBuilder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ModifyRecruitmentPositionRequest {

    @Valid
    @Size(max = RECRUITMENT_POSITION_LIMIT)
    private List<CreateRecruitmentPositionRequest> createRecruitmentPositionRequests;

    @Valid
    @Size(max = RECRUITMENT_POSITION_LIMIT)
    private List<UpdateRecruitmentPositionRequest> updateRecruitmentPositionRequests;

    @Size(max = RECRUITMENT_POSITION_LIMIT)
    private List<Long> recruitmentPositionIdsToRemove;


}
