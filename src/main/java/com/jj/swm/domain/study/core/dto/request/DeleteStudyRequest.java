package com.jj.swm.domain.study.core.dto.request;

import com.jj.swm.global.common.constants.PageSize;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeleteStudyRequest {

    @NotEmpty
    @Size(max = PageSize.Study)
    private List<Long> studyIds;
}
