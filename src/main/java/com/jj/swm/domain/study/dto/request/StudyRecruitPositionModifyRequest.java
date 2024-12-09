package com.jj.swm.domain.study.dto.request;

import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyRecruitPositionModifyRequest {

    @Size(max = 100)
    private List<StudyRecruitPositionsCreateRequest> newRecruitPositions;

    @Size(max = 100)
    private List<StudyRecruitPositionUpdateRequest> updatedRecruitPositions;

    @Size(max = 100)
    private List<Long> deletedRecruitPositionIds;
}
