package com.jj.swm.domain.user.core.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateInspectionStatusRequest {

    @NotEmpty
    private List<Long> businessVerificationRequestIds;
}
