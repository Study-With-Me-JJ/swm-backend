package com.jj.swm.domain.study.core.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

import static com.jj.swm.domain.study.core.constants.StudyConstants.TAG_LIMIT;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ModifyStudyTagRequest {

    @Valid
    @Size(max = TAG_LIMIT)
    private List<@Size(max = 50) String> tagsToAdd;

    @Size(max = TAG_LIMIT)
    private List<Long> tagIdsToRemove;
}
