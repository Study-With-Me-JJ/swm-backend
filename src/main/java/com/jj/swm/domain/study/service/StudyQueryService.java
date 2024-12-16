package com.jj.swm.domain.study.service;

import com.jj.swm.domain.study.dto.ReplyCountInfo;
import com.jj.swm.domain.study.dto.StudyBookmarkInfo;
import com.jj.swm.domain.study.dto.StudyInquiryCondition;
import com.jj.swm.domain.study.dto.response.*;
import com.jj.swm.domain.study.entity.Study;
import com.jj.swm.domain.study.entity.StudyComment;
import com.jj.swm.domain.study.entity.StudyImage;
import com.jj.swm.domain.study.entity.StudyRecruitmentPosition;
import com.jj.swm.domain.study.repository.*;
import com.jj.swm.global.common.dto.PageResponse;
import com.jj.swm.global.common.enums.ErrorCode;
import com.jj.swm.global.exception.GlobalException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudyQueryService {

    private final StudyRepository studyRepository;
    private final CommentRepository commentRepository;
    private final StudyLikeRepository studyLikeRepository;
    private final StudyImageRepository studyImageRepository;
    private final StudyBookmarkRepository studyBookmarkRepository;
    private final StudyRecruitmentPositionRepository studyRecruitmentPositionRepository;

    @Value("${study.page.size}")
    private int studyPageSize;

    @Value("${study.comment.page.size}")
    private int commentPageSize;

    public PageResponse<StudyInquiryResponse> getList(
            UUID userId,
            StudyInquiryCondition inquiryCondition
    ) {
        List<Study> studies = studyRepository.findAllWithUserAndTags(studyPageSize + 1, inquiryCondition);

        if (studies.isEmpty()) {
            throw new GlobalException(ErrorCode.NOT_FOUND, "studies not found");
        }

        boolean hasNext = studies.size() > studyPageSize;

        List<Study> pagedStudies = hasNext ? studies.subList(0, studyPageSize) : studies;

        List<Long> studyIds = pagedStudies.stream()
                .map(Study::getId)
                .toList();

        Map<Long, Long> bookmarkIdByStudyId = userId != null
                ? studyBookmarkRepository.findAllByUserIdAndStudyIds(userId, studyIds)
                    .stream()
                    .collect(Collectors.toMap(StudyBookmarkInfo::getId, StudyBookmarkInfo::getStudyId))
                : Collections.emptyMap();

        List<StudyInquiryResponse> inquiryResponses = pagedStudies.stream()
                .map(study -> StudyInquiryResponse.of(
                        study,
                        bookmarkIdByStudyId.getOrDefault(study.getId(), null),
                        study.getUser().getId().equals(userId)
                ))
                .toList();

        return PageResponse.of(inquiryResponses, hasNext);
    }

    @Transactional
    public StudyDetailsResponse get(UUID userId, Long studyId) {
        Study study = studyRepository.findByIdWithPessimisticLock(studyId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "study not found"));

        boolean likeStatus = false;
        if (userId != null) {
            likeStatus = studyLikeRepository.existsByUserIdAndStudyId(userId, studyId);
        }

        study.incrementViewCount();

        List<StudyImage> images = studyImageRepository.findAllByStudyId(studyId);

        List<StudyImageInquiryResponse> imageInquiryResponses = images.stream()
                .map(StudyImageInquiryResponse::from)
                .toList();

        List<StudyRecruitmentPosition> recruitmentPositions =
                studyRecruitmentPositionRepository.findAllByStudyId(studyId);

        List<StudyRecruitPositionInquiryResponse> recruitPositionInquiryResponses = recruitmentPositions.stream()
                .map(StudyRecruitPositionInquiryResponse::from)
                .toList();

        Pageable pageable = PageRequest.of(0, commentPageSize, Sort.by("id").descending());
        Page<StudyComment> pageComments = commentRepository.findCommentWithUserByStudyId(studyId, pageable);

        List<Long> parentIds = pageComments.get().map(StudyComment::getId).toList();

        Map<Long, Integer> replyCountByParentId = commentRepository.countReplyByParentId(parentIds).stream()
                .collect(Collectors.toMap(ReplyCountInfo::getParentId, ReplyCountInfo::getReplyCount));

        List<CommentInquiryResponse> commentInquiryResponses = pageComments.get()
                .map(comment -> CommentInquiryResponse.of(
                        comment, replyCountByParentId.getOrDefault(comment.getId(), 0))
                ).toList();

        PageResponse<CommentInquiryResponse> commentPageResponse = PageResponse.of(
                pageComments.getNumberOfElements(),
                pageComments.getTotalPages(),
                pageComments.getTotalElements(),
                pageComments.hasNext(),
                commentInquiryResponses
        );

        return StudyDetailsResponse.of(
                likeStatus,
                study.getViewCount(),
                study.getOpenChatUrl(),
                imageInquiryResponses,
                recruitPositionInquiryResponses,
                commentPageResponse
        );
    }
}
