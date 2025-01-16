package com.jj.swm.domain.user.service;

import com.jj.swm.domain.user.dto.response.GetBusinessVerificationRequestResponse;
import com.jj.swm.domain.user.entity.BusinessVerificationRequest;
import com.jj.swm.domain.user.entity.InspectionStatus;
import com.jj.swm.domain.user.repository.BusinessVerificationRequestRepository;
import com.jj.swm.domain.user.repository.UserCredentialRepository;
import com.jj.swm.domain.user.repository.UserRepository;
import com.jj.swm.global.common.dto.PageResponse;
import com.jj.swm.global.common.enums.PageSize;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserQueryService {

    private final UserRepository userRepository;
    private final UserCredentialRepository userCredentialRepository;
    private final BusinessVerificationRequestRepository businessVerificationRequestRepository;

    public boolean validateLoginId(String loginId) {
        return userCredentialRepository.existsByLoginId(loginId);
    }

    public boolean validateNickname(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    @Transactional(readOnly = true)
    public PageResponse<GetBusinessVerificationRequestResponse> getBusinessVerificationRequests(
            List<InspectionStatus> status, int pageNo
    ) {
        Pageable pageable = PageRequest.of(
                pageNo,
                PageSize.BusinessVerificationRequest,
                Sort.by("id").descending()
        );

        Page<BusinessVerificationRequest> pagedRequests
                = businessVerificationRequestRepository.findPagedVerificationRequestWithStatus(status, pageable);

        return PageResponse.of(pagedRequests, GetBusinessVerificationRequestResponse::from);
    }
}
