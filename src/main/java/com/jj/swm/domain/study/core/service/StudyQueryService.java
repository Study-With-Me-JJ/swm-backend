package com.jj.swm.domain.study.core.service;

import com.jj.swm.domain.study.comment.dto.response.ParentCommentInquiryResponse;
import com.jj.swm.domain.study.core.dto.StudyBookmarkInfo;
import com.jj.swm.domain.study.core.dto.StudyInquiryCondition;
import com.jj.swm.domain.study.core.dto.response.StudyDetailsResponse;
import com.jj.swm.domain.study.core.dto.response.StudyImageInquiryResponse;
import com.jj.swm.domain.study.core.dto.response.StudyInquiryResponse;
import com.jj.swm.domain.study.recruitmentposition.dto.response.RecruitPositionInquiryResponse;
import com.jj.swm.domain.study.core.entity.Study;
import com.jj.swm.domain.study.core.entity.StudyImage;
import com.jj.swm.domain.study.recruitmentposition.entity.StudyRecruitmentPosition;
import com.jj.swm.domain.study.core.repository.StudyBookmarkRepository;
import com.jj.swm.domain.study.core.repository.StudyImageRepository;
import com.jj.swm.domain.study.core.repository.StudyLikeRepository;
import com.jj.swm.domain.study.core.repository.StudyRepository;
import com.jj.swm.domain.study.recruitmentposition.repository.RecruitmentPositionRepository;
import com.jj.swm.domain.study.comment.service.CommentQueryService;
import com.jj.swm.global.common.dto.PageResponse;
import com.jj.swm.global.common.enums.ErrorCode;
import com.jj.swm.global.common.enums.PageSize;
import com.jj.swm.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudyQueryService {

    private final StudyRepository studyRepository;
    private final CommentQueryService commentQueryService;
    private final StudyLikeRepository studyLikeRepository;
    private final StudyImageRepository studyImageRepository;
    private final StudyBookmarkRepository studyBookmarkRepository;
    private final RecruitmentPositionRepository recruitmentPositionRepository;

    @Transactional(readOnly = true)
    public PageResponse<StudyInquiryResponse> getList(UUID userId, StudyInquiryCondition inquiryCondition) {
        List<Study> studies = studyRepository.findPagedStudiesByCondition(PageSize.Study + 1, inquiryCondition);

        if (studies.isEmpty()) {
            return PageResponse.of(List.of(), false);
        }

        boolean hasNext = studies.size() > PageSize.Study;

        List<Study> pagedStudies = hasNext ? studies.subList(0, PageSize.Study) : studies;

        Map<Long, Long> bookmarkIdByStudyId = getBookmarkMapping(userId, pagedStudies);

        List<StudyInquiryResponse> inquiryResponses = loadStudyInquiryResponse(pagedStudies, bookmarkIdByStudyId);

        return PageResponse.of(inquiryResponses, hasNext);
    }

    public Map<Long, Long> getBookmarkMapping(UUID userId, List<Study> studies) {
        List<Long> studyIds = studies.stream()
                .map(Study::getId)
                .toList();

        return userId != null
                ? studyBookmarkRepository.findAllByUserIdAndStudyIds(userId, studyIds)
                .stream()
                .collect(Collectors.toMap(StudyBookmarkInfo::getStudyId, StudyBookmarkInfo::getId))
                : Collections.emptyMap();
    }

    private List<StudyInquiryResponse> loadStudyInquiryResponse(
            List<Study> pagedStudies, Map<Long, Long> bookmarkIdByStudyId
    ) {
        return pagedStudies.stream()
                .map(study -> StudyInquiryResponse.of(
                        study, bookmarkIdByStudyId.getOrDefault(study.getId(), null)
                ))
                .toList();
    }

    @Transactional
    public StudyDetailsResponse get(UUID userId, Long studyId) {
        Study study = studyRepository.findByIdWithUserUsingPessimisticLock(studyId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "study not found"));

        boolean likeStatus = isLike(userId, studyId);

        study.incrementViewCount();

        List<StudyImage> images = studyImageRepository.findAllByStudyId(studyId);

        List<StudyImageInquiryResponse> imageInquiryResponses = images.stream()
                .map(StudyImageInquiryResponse::from)
                .toList();

        List<StudyRecruitmentPosition> recruitmentPositions = recruitmentPositionRepository.findAllByStudyId(studyId);

        List<RecruitPositionInquiryResponse> recruitPositionInquiryResponses = recruitmentPositions.stream()
                .map(RecruitPositionInquiryResponse::from)
                .toList();

        Pageable pageable = PageRequest.of(
                0,
                PageSize.StudyComment,
                Sort.by("id").descending()
        );
        PageResponse<ParentCommentInquiryResponse> commentPageResponse =
                commentQueryService.loadCommentPageResponse(studyId, pageable);

        return StudyDetailsResponse.of(
                likeStatus,
                study,
                imageInquiryResponses,
                recruitPositionInquiryResponses,
                commentPageResponse
        );
    }

    private boolean isLike(UUID userId, Long studyId) {
        boolean likeStatus = false;

        if (userId != null) {
            likeStatus = studyLikeRepository.existsByUserIdAndStudyId(userId, studyId);
        }
        return likeStatus;
    }
}
