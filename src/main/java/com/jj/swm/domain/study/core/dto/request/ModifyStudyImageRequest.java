package com.jj.swm.domain.study.core.dto.request;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

import static com.jj.swm.domain.study.core.constants.StudyConstants.IMAGE_LIMIT;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ModifyStudyImageRequest {

    @Valid
    @Size(max = IMAGE_LIMIT)
    private List<@Size(max = 300) String> imageUrlsToAdd;

    @Size(max = IMAGE_LIMIT)
    private List<Long> imageIdsToRemove;
}
