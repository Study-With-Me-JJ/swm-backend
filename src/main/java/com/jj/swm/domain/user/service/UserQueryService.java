package com.jj.swm.domain.user.service;

import com.jj.swm.domain.study.dto.response.StudyInquiryResponse;
import com.jj.swm.domain.study.entity.Study;
import com.jj.swm.domain.study.repository.StudyLikeRepository;
import com.jj.swm.domain.study.service.StudyQueryService;
import com.jj.swm.domain.user.dto.response.GetBusinessVerificationRequestResponse;
import com.jj.swm.domain.user.dto.response.GetUserInfoResponse;
import com.jj.swm.domain.user.entity.BusinessVerificationRequest;
import com.jj.swm.domain.user.entity.InspectionStatus;
import com.jj.swm.domain.user.entity.User;
import com.jj.swm.domain.user.repository.BusinessVerificationRequestRepository;
import com.jj.swm.domain.user.repository.UserCredentialRepository;
import com.jj.swm.domain.user.repository.UserRepository;
import com.jj.swm.global.common.dto.PageResponse;
import com.jj.swm.global.common.enums.ErrorCode;
import com.jj.swm.global.common.enums.PageSize;
import com.jj.swm.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserQueryService {

    private final UserRepository userRepository;
    private final UserCredentialRepository userCredentialRepository;
    private final BusinessVerificationRequestRepository businessVerificationRequestRepository;
    private final StudyLikeRepository studyLikeRepository;
    private final StudyQueryService studyQueryService;

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

    @Transactional(readOnly = true)
    public GetUserInfoResponse getUserInfo(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_VALID, "User Not Found"));

        return GetUserInfoResponse.from(user);
    }

    public boolean validateLoginId(String loginId) {
        return userCredentialRepository.existsByLoginId(loginId);
    }

    public boolean validateNickname(String nickname) {
        return userRepository.existsByNickname(nickname);
    }


    @Transactional(readOnly = true)
    public PageResponse<StudyInquiryResponse> getLikedStudies(UUID userId, int pageNo) {
        Pageable pageable = PageRequest.of(
                pageNo,
                PageSize.Study,
                Sort.by("id").descending()
        );

        Page<Study> pagedStudies = studyLikeRepository.findStudiesByUserId(userId, pageable);

        Map<Long, Long> bookmarkIdByStudyId = studyQueryService.getBookmarkMapping(userId, pagedStudies.getContent());

        return PageResponse.of(
                pagedStudies,
                (study) -> StudyInquiryResponse.of(study, bookmarkIdByStudyId.getOrDefault(study.getId(), null))
        );
    }
}
