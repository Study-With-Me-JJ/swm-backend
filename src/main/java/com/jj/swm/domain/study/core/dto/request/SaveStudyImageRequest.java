package com.jj.swm.domain.study.core.dto.request;


import com.jj.swm.domain.study.constants.StudyElementLimit;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SaveStudyImageRequest {

    @Size(max = StudyElementLimit.IMAGE)
    private List<String> imageUrlListToAdd;

    @Size(max = StudyElementLimit.IMAGE)
    private List<Long> imageIdListToRemove;
}
