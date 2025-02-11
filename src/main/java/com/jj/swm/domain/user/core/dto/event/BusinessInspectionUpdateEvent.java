package com.jj.swm.domain.user.core.dto.event;

import com.jj.swm.domain.user.core.entity.BusinessVerificationRequest;
import com.jj.swm.domain.user.core.entity.InspectionStatus;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BusinessInspectionUpdateEvent {

    private String userNickname;
    private String userEmail;
    private InspectionStatus status;
    private String businessName;

    public static BusinessInspectionUpdateEvent of(
            BusinessVerificationRequest businessVerificationRequest, InspectionStatus status
    ) {
        return BusinessInspectionUpdateEvent.builder()
                .userNickname(businessVerificationRequest.getUserNickname())
                .userEmail(businessVerificationRequest.getUserEmail())
                .status(status)
                .businessName(businessVerificationRequest.getBusinessName())
                .build();
    }
}
