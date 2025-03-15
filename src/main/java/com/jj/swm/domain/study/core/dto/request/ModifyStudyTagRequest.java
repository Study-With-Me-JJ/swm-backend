package com.jj.swm.domain.study.core.dto.request;

import com.jj.swm.domain.study.constants.StudyConstants;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ModifyStudyTagRequest {

    @Size(max = StudyConstants.TAG_LIMIT)
    private List<String> tagsToAdd;

    @Size(max = StudyConstants.TAG_LIMIT)
    private List<Long> tagIdsToRemove;
}
