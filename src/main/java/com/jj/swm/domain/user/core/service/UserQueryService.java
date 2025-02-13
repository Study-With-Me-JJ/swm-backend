package com.jj.swm.domain.user.core.service;

import com.jj.swm.domain.study.core.dto.response.FindStudyResponse;
import com.jj.swm.domain.study.core.entity.Study;
import com.jj.swm.domain.study.core.entity.StudyBookmark;
import com.jj.swm.domain.study.core.repository.StudyBookmarkRepository;
import com.jj.swm.domain.study.core.repository.StudyLikeRepository;
import com.jj.swm.domain.study.core.service.StudyQueryService;
import com.jj.swm.domain.user.core.dto.response.GetBusinessVerificationRequestResponse;
import com.jj.swm.domain.user.core.dto.response.GetUserInfoResponse;
import com.jj.swm.domain.user.core.entity.BusinessVerificationRequest;
import com.jj.swm.domain.user.core.entity.InspectionStatus;
import com.jj.swm.domain.user.core.entity.User;
import com.jj.swm.domain.user.core.repository.BusinessVerificationRequestRepository;
import com.jj.swm.domain.user.core.repository.UserCredentialRepository;
import com.jj.swm.domain.user.core.repository.UserRepository;
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
    private final StudyQueryService studyQueryService;
    private final StudyLikeRepository studyLikeRepository;
    private final StudyBookmarkRepository studyBookmarkRepository;

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

    @Transactional(readOnly = true)
    public PageResponse<FindStudyResponse> findLikedStudyList(UUID userId, int pageNo) {
        Pageable pageable = PageRequest.of(
                pageNo,
                PageSize.Study,
                Sort.by("id").descending()
        );

        Page<Study> pagedStudy = studyLikeRepository.findPagedStudyByUserId(userId, pageable);

        Map<Long, Long> bookmarkIdByStudyId = studyQueryService.loadBookmarkMapping(userId, pagedStudy.getContent());

        return PageResponse.of(
                pagedStudy,
                (study) -> FindStudyResponse.of(study, bookmarkIdByStudyId.getOrDefault(study.getId(), null))
        );
    }

    @Transactional(readOnly = true)
    public PageResponse<FindStudyResponse> findBookmarkedStudyList(UUID userId, int pageNo) {
        Pageable pageable = PageRequest.of(
                pageNo,
                PageSize.Study,
                Sort.by("id").descending()
        );

        Page<StudyBookmark> pagedStudyBookmark = studyBookmarkRepository.findPagedBookmarkByUserIdWithStudy(
                userId, pageable
        );

        return PageResponse.of(pagedStudyBookmark, FindStudyResponse::of);
    }

    public boolean validateLoginId(String loginId) {
        return userCredentialRepository.existsByLoginId(loginId);
    }

    public boolean validateNickname(String nickname) {
        return userRepository.existsByNickname(nickname);
    }
}
