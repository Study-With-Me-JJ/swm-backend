package com.jj.swm.domain.study.core.dto.request;

import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SaveStudyTagRequest {

    @Size(max = 100)
    private List<String> tagListToAdd;

    @Size(max = 100)
    private List<Long> tagIdListToRemove;
}
