package com.jj.swm.domain.user.dto.response;

import com.jj.swm.domain.user.entity.BusinessVerificationRequest;
import com.jj.swm.domain.user.entity.InspectionStatus;
import com.jj.swm.domain.user.entity.RoleType;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class GetBusinessVerificationRequestResponse {

    private Long id;
    private UUID userId;
    private String userName;
    private RoleType userRole;
    private String userNickname;
    private String businessNumber;
    private String businessOwnerName;
    private String businessRegistrationDate;
    private String businessName;
    private InspectionStatus inspectionStatus;

    public static GetBusinessVerificationRequestResponse from(BusinessVerificationRequest request) {
        return GetBusinessVerificationRequestResponse.builder()
                .id(request.getId())
                .userId(request.getUser().getId())
                .userName(request.getUserName())
                .userRole(request.getUserRole())
                .userNickname(request.getUserNickname())
                .businessNumber(request.getBusinessNumber())
                .businessOwnerName(request.getBusinessOwnerName())
                .businessRegistrationDate(request.getBusinessRegistrationDate())
                .businessName(request.getBusinessName())
                .inspectionStatus(request.getInspectionStatus())
                .build();
    }
}
