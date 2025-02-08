package com.jj.swm.domain.study.dto.core.request;

import com.jj.swm.domain.study.entity.core.StudyStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyStatusUpdateRequest {

    @NotNull
    private StudyStatus status;
}
