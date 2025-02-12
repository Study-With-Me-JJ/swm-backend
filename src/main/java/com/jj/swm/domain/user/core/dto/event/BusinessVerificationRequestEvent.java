package com.jj.swm.domain.user.core.dto.event;

import com.jj.swm.domain.user.core.entity.BusinessVerificationRequest;
import com.jj.swm.domain.user.core.entity.RoleType;
import lombok.*;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BusinessVerificationRequestEvent {

    private UUID userId;
    private String userName;
    private RoleType userRole;
    private String userNickname;
    private String userEmail;
    private String businessNumber;
    private String businessOwnerName;
    private String businessRegistrationDate;
    private String businessName;

    public static BusinessVerificationRequestEvent from(BusinessVerificationRequest request) {
        return BusinessVerificationRequestEvent.builder()
                .userId(request.getUser().getId())
                .userName(request.getUserName())
                .userRole(request.getUserRole())
                .userNickname(request.getUserNickname())
                .userEmail(request.getUserEmail())
                .businessNumber(request.getBusinessNumber())
                .businessOwnerName(request.getBusinessOwnerName())
                .businessRegistrationDate(request.getBusinessRegistrationDate())
                .businessName(request.getBusinessName())
                .build();
    }
}
